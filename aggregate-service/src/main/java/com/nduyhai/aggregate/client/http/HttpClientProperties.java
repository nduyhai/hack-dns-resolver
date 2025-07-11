package com.nduyhai.aggregate.client.http;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "greeting.http")
public class HttpClientProperties {
    private String url = "http://localhost:8080";

    // SSL properties
    private boolean sslEnabled = false;
    private boolean sslVerify = true;

    // Connection pool properties
    private int maxConnTotal = 100;
    private int maxConnPerRoute = 20;
    private Duration socketTimeout = Duration.ofSeconds(30);

    // Request timeout properties
    private Duration connectionRequestTimeout = Duration.ofSeconds(30);
    private Duration responseTimeout = Duration.ofSeconds(30);
}
