package com.nduyhai.aggregate.config;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ClientInterceptor;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    @Bean
    @GRpcGlobalInterceptor
    public ClientInterceptor tracingClientInterceptor(Tracing tracing) {
        return GrpcTracing.create(tracing).newClientInterceptor();
    }
}
