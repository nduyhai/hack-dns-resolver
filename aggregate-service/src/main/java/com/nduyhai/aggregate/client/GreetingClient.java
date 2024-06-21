package com.nduyhai.aggregate.client;

import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import com.nduyhai.grpc.Greeting;
import com.nduyhai.grpc.GreetingServiceGrpc;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class GreetingClient {
    private final GrpcClientProperties greetingProperties;
    private final List<ClientInterceptor> interceptors;
    private final Executor greetingThreadPool;
    private GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        ManagedChannelBuilder<?> builder = NettyChannelBuilder
                .forAddress(this.greetingProperties.getHost(), this.greetingProperties.getPort())
                .executor(this.greetingThreadPool)
                .enableRetry()
                .maxRetryAttempts(this.greetingProperties.getMaxRetries())
                .defaultLoadBalancingPolicy("round_robin")
                .intercept(interceptors)
                .keepAliveTimeout(this.greetingProperties.getKeepAlive().toMillis(), TimeUnit.MILLISECONDS);

        if (!this.greetingProperties.isSsl()) {
            builder.usePlaintext();
        }
        blockingStub = GreetingServiceGrpc.newBlockingStub(builder.build());
    }

    public GreetingResponse executeGreeting(GreetingRequest request) {
        Greeting.HelloResponse response = this.blockingStub
                .withDeadlineAfter(this.greetingProperties.getTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .hello(Greeting.HelloRequest.newBuilder().setName(request.getName()).build());
        return new GreetingResponse(response.getGreeting());
    }

}
