package com.nduyhai.aggregate.controller;

import com.nduyhai.aggregate.client.GreetingClient;
import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class GreetingController {
    private final GreetingClient greetingClient;

    @PostMapping("/greeting")
    public ResponseEntity<GreetingResponse> greeting(@RequestBody @Validated GreetingRequest request) {
        return ResponseEntity.ok(this.greetingClient.executeGreeting(request));
    }

}
