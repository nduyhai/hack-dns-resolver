package com.nduyhai.greeting.controller;

import com.nduyhai.greeting.dto.GreetingRequest;
import com.nduyhai.greeting.dto.GreetingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GreetingController {

    @PostMapping("/greeting")
    public ResponseEntity<GreetingResponse> greeting(@RequestBody GreetingRequest request) {
        log.info("Handling HTTP greeting request");
        return ResponseEntity.ok(new GreetingResponse("Hello, " + request.getName()));
    }
}