package com.nduyhai.aggregate.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GreetingClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "greeting")
    public GrpcClientProperties greetingProperties() {
        return new GrpcClientProperties();
    }
}
