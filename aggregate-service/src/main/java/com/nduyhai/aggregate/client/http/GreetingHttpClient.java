package com.nduyhai.aggregate.client.http;

import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Service
public class GreetingHttpClient {
    private final RestTemplate restTemplate;
    private final HttpClientProperties properties;

    public GreetingResponse executeGreeting(GreetingRequest request) {
        log.info("Executing HTTP greeting request to {}", properties.getUrl());
        GreetingResponse response = restTemplate.postForObject(
                properties.getUrl() + "/greeting",
                request,
                GreetingResponse.class
        );
        log.info("HTTP greeting request completed - check logs for HTTP protocol version");
        return response;
    }
}
