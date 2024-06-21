package com.nduyhai.aggregate.client;

import io.grpc.internal.GrpcUtil;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class GreetingClientConfig {
    public static final String NAME = "pool-hacked";


    @Bean
    @ConfigurationProperties(prefix = "greeting")
    public GrpcClientProperties greetingProperties() {
        return new GrpcClientProperties();
    }

    @Bean
    public Executor greetingThreadPool() {
        return Executors.newCachedThreadPool(GrpcUtil.getThreadFactory(NAME + "-%d", true));
    }

    @Bean
    public ExecutorServiceMetrics grpcThreadPoolMetrics(Executor greetingThreadPool) {
        return new ExecutorServiceMetrics((ExecutorService) greetingThreadPool, "hackedPool", "grpc_pool", Tags.of("target", "client"));
    }
}
