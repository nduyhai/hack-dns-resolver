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

@RequiredArgsConstructor
@Service
public class GreetingClient {
    private final GrpcClientProperties greetingProperties;
    private final List<ClientInterceptor> interceptors;
    private GreetingServiceGrpc.GreetingServiceBlockingStub blockingStub;

    @PostConstruct
    public void init() {
        ManagedChannelBuilder<?> builder = NettyChannelBuilder
                .forAddress(this.greetingProperties.getHost(), this.greetingProperties.getPort())
                .enableRetry()
                .maxRetryAttempts(this.greetingProperties.getMaxRetries())
                .defaultLoadBalancingPolicy("round_robin")
                .intercept(interceptors);

        if (!this.greetingProperties.isSsl()) {
            builder.usePlaintext();
        }
        blockingStub = GreetingServiceGrpc.newBlockingStub(builder.build());
    }

    public GreetingResponse executeGreeting(GreetingRequest request) {
        Greeting.HelloResponse response = this.blockingStub.hello(Greeting.HelloRequest.newBuilder().setName(request.getName()).build());
        return new GreetingResponse(response.getGreeting());
    }

}
