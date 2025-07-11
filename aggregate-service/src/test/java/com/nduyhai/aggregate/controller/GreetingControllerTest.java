package com.nduyhai.aggregate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nduyhai.aggregate.client.grpc.GreetingClient;
import com.nduyhai.aggregate.client.http.GreetingHttpClient;
import com.nduyhai.aggregate.client.webflux.GreetingWebFluxClient;
import com.nduyhai.aggregate.dto.GreetingRequest;
import com.nduyhai.aggregate.dto.GreetingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GreetingController.class)
public class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GreetingClient greetingClient;

    @MockBean
    private GreetingHttpClient greetingHttpClient;

    @MockBean
    private GreetingWebFluxClient greetingWebFluxClient;

    @Test
    public void testGreetingHttp() throws Exception {
        GreetingRequest request = new GreetingRequest();
        request.setName("World");

        GreetingResponse response = new GreetingResponse("Hello, World");

        when(greetingHttpClient.executeGreeting(any(GreetingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/greeting-http")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.greeting").value("Hello, World"));
    }

    @Test
    public void testGreetingWebFlux() throws Exception {
        GreetingRequest request = new GreetingRequest();
        request.setName("WebFlux");

        GreetingResponse response = new GreetingResponse("Hello, WebFlux");

        when(greetingWebFluxClient.executeGreeting(any(GreetingRequest.class)))
                .thenReturn(Mono.just(response));

        // For reactive endpoints with MockMvc, we can only verify the status code
        // In a real application, we would use WebTestClient instead of MockMvc
        mockMvc.perform(post("/greeting-webflux")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
