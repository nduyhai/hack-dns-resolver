package com.nduyhai.aggregate.config;

import com.nduyhai.aggregate.client.http.HttpClientProperties;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientConfig.class);
    /**
     * HTTP response interceptor that logs the HTTP protocol version used for the request
     */
    private static final HttpResponseInterceptor PROTOCOL_VERSION_LOGGER = (response, entity, context) -> {
        String protocolVersion = response.getVersion().toString();
        logger.info("HTTP Protocol Version: {}", protocolVersion);
    };
    private final HttpClientProperties properties;

    public HttpClientConfig(HttpClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Create SSL context
        SSLContext sslContext = null;
        if (properties.isSslEnabled()) {
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            if (!properties.isSslVerify()) {
                sslContextBuilder.loadTrustMaterial(null, (x509Certificates, s) -> true);
            }
            sslContext = sslContextBuilder.build();
        }

        // Create connection pool builder
        PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnTotal(properties.getMaxConnTotal())
                .setMaxConnPerRoute(properties.getMaxConnPerRoute())
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setDefaultSocketConfig(
                        SocketConfig.custom()
                                .setSoTimeout(Timeout.of(properties.getSocketTimeout().toSeconds(), TimeUnit.SECONDS))
                                .build());

        // Add SSL configuration if enabled
        if (properties.isSslEnabled() && sslContext != null) {
            var sslConnectionSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .setHostnameVerifier(properties.isSslVerify() ? null : NoopHostnameVerifier.INSTANCE)
                    .build();
            connectionManagerBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
        }

        // Build connection manager
        var connectionManager = connectionManagerBuilder.build();

        // Configure request timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(properties.getConnectionRequestTimeout().toSeconds(), TimeUnit.SECONDS))
                .setResponseTimeout(Timeout.of(properties.getResponseTimeout().toSeconds(), TimeUnit.SECONDS))
                .build();

        // Build HTTP client with Apache HTTP 5 client
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .addResponseInterceptorLast(PROTOCOL_VERSION_LOGGER)
                .build();

        // Create request factory with Apache HTTP 5 client
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Create RestTemplate with Apache HTTP 5 client
        return new RestTemplate(requestFactory);
    }
}
