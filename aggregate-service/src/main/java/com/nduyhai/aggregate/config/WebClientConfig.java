package com.nduyhai.aggregate.config;

import com.nduyhai.aggregate.client.http.HttpClientProperties;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    private final HttpClientProperties properties;

    public WebClientConfig(HttpClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WebClient webClient() throws SSLException {
        // Create a connection provider with connection pooling
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
                .maxConnections(properties.getMaxConnTotal())
                .maxIdleTime(Duration.ofSeconds(60))
                .build();

        // Configure HTTP client with HTTP/2 support and connection pooling
        HttpClient httpClient = HttpClient.create(provider)
                .responseTimeout(Duration.ofSeconds(properties.getResponseTimeout().toSeconds()))
                // Enable wiretap for detailed logging including protocol version
                .wiretap("reactor.netty.http.client.HttpClient", 
                        org.slf4j.LoggerFactory.getLogger("reactor.netty.http.client.HttpClient").isDebugEnabled() 
                        ? io.netty.handler.logging.LogLevel.DEBUG 
                        : io.netty.handler.logging.LogLevel.INFO)
                // Add hooks to log HTTP version information
                .doOnRequest((httpRequest, connection) -> {
                    logger.info("WebClient HTTP Request Version: {}", httpRequest.version());
                })
                .doOnResponse((httpResponse, connection) -> {
                    logger.info("WebClient HTTP Response Version: {}", httpResponse.version());
                });

        // Configure SSL if enabled
        if (properties.isSslEnabled()) {
            SslContext sslContext;
            if (!properties.isSslVerify()) {
                // Use insecure trust manager for development/testing
                sslContext = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE)
                        .build();
            } else {
                // Use default SSL context for production
                sslContext = SslContextBuilder.forClient().build();
            }

            httpClient = httpClient.secure(spec -> spec.sslContext(sslContext));
        }

        // Enable HTTP/2
        if (properties.isHttp2Enabled()) {
            httpClient = httpClient.protocol(HttpProtocol.H2, HttpProtocol.H2C, HttpProtocol.HTTP11);
            logger.info("HTTP/2 protocol enabled for WebClient with HTTP/1.1 fallback");
        }

        // Configure exchange strategies with increased memory limits
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB buffer
                .build();

        // Create WebClient with configured HttpClient and exchange strategies
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .filter(logRequest())
                .build();
    }

    // Log request filter function
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("WebClient Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> 
                values.forEach(value -> logger.debug("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }
}
