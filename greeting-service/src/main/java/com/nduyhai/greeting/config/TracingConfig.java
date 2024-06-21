package com.nduyhai.greeting.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    @Bean
    public ServerInterceptor tracingServerInterceptor(Tracing tracing) {
        return GrpcTracing.create(tracing).newServerInterceptor();
    }
}
