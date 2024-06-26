package com.nduyhai.aggregate.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;

@Getter
@Setter
public class GrpcClientProperties {
    private String host;
    private int port;
    private boolean ssl;
    private int maxRetries;
    private Duration timeout;
    private Duration keepAlive;
}
