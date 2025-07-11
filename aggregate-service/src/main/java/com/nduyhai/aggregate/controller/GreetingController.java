package com.nduyhai.aggregate.controller;

import com.nduyhai.aggregate.client.grpc.GreetingClient;
import com.nduyhai.aggregate.client.http.GreetingHttpClient;
import com.nduyhai.aggregate.client.webflux.GreetingWebFluxClient;
import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GreetingController {
    private final GreetingClient greetingClient;
    private final GreetingHttpClient greetingHttpClient;
    private final GreetingWebFluxClient greetingWebFluxClient;

    @PostMapping("/greeting")
    public ResponseEntity<GreetingResponse> greeting(@RequestBody @Validated GreetingRequest request) {
        return ResponseEntity.ok(this.greetingClient.executeGreeting(request));
    }

    @PostMapping("/greeting-http")
    public ResponseEntity<GreetingResponse> greetingHttp(@RequestBody @Validated GreetingRequest request) {
        return ResponseEntity.ok(this.greetingHttpClient.executeGreeting(request));
    }

    @PostMapping("/greeting-webflux")
    public Mono<ResponseEntity<GreetingResponse>> greetingWebFlux(@RequestBody @Validated GreetingRequest request) {
        return this.greetingWebFluxClient.executeGreeting(request)
                .map(ResponseEntity::ok);
    }
}
