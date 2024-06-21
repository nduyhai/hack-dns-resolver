package com.nduyhai.greeting.config;

import io.grpc.ServerBuilder;
import io.grpc.internal.GrpcUtil;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.lognet.springboot.grpc.GRpcServerBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ServerPoolConfig {
    public static final String NAME = "pool-hacked";

    @Bean
    public Executor grpcThreadPool() {
        return Executors.newCachedThreadPool(GrpcUtil.getThreadFactory(NAME + "-%d", true));
    }

    @Bean
    public GRpcServerBuilderConfigurer poolConfigurer(Executor grpcThreadPool) {
        return new GRpcServerBuilderConfigurer() {
            @Override
            public void configure(ServerBuilder<?> serverBuilder) {
                serverBuilder.executor(grpcThreadPool);
            }

        };
    }


    @Bean
    public ExecutorServiceMetrics grpcThreadPoolMetrics(Executor grpcThreadPool) {
        return new ExecutorServiceMetrics((ExecutorService) grpcThreadPool, "hackedPool", "grpc_pool", Tags.of("target", "server"));
    }
}

