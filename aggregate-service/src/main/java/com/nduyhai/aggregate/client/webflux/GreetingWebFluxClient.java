package com.nduyhai.aggregate.client.webflux;

import com.nduyhai.aggregate.client.http.HttpClientProperties;
import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class GreetingWebFluxClient {
    private final WebClient webClient;
    private final HttpClientProperties properties;

    public Mono<GreetingResponse> executeGreeting(GreetingRequest request) {
        log.info("Executing WebFlux greeting request to {}", properties.getUrl());
        
        return webClient.post()
                .uri(properties.getUrl() + "/greeting")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GreetingResponse.class)
                .doOnSuccess(response -> log.info("WebFlux greeting request completed successfully"))
                .doOnError(error -> log.error("WebFlux greeting request failed", error));
    }
}