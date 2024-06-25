package com.nduyhai.aggregate.client;

import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import com.nduyhai.grpc.Greeting;
import com.nduyhai.grpc.GreetingServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class GreetingClient {
    private final GrpcClientProperties greetingProperties;
    private final List<ClientInterceptor> interceptors;
    private final Executor greetingThreadPool;
    private GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        log.info("Config {}", this.greetingProperties);
        ManagedChannelBuilder<?> builder = NettyChannelBuilder
                .forAddress(this.greetingProperties.getHost(), this.greetingProperties.getPort())
                .executor(this.greetingThreadPool)
                .enableRetry()
                .maxRetryAttempts(this.greetingProperties.getMaxRetries())
                .defaultLoadBalancingPolicy("round_robin")
                .intercept(interceptors)
                .keepAliveTime(this.greetingProperties.getKeepAlive().toMillis(), TimeUnit.MILLISECONDS);

        if (!this.greetingProperties.isSsl()) {
            builder.usePlaintext();
        }
        blockingStub = GreetingServiceGrpc.newBlockingStub(builder.build());
    }

    public GreetingResponse executeGreeting(GreetingRequest request) {
        long ms = ThreadLocalRandom.current().nextLong(this.greetingProperties.getTimeout().toMillis());
        Greeting.HelloResponse response = this.blockingStub
                .withDeadlineAfter(ms, TimeUnit.MILLISECONDS)
                .hello(Greeting.HelloRequest.newBuilder().setName(request.getName()).build());
        return new GreetingResponse(response.getGreeting());
    }

}
