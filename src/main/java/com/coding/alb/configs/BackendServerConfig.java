package com.coding.alb.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class BackendServerConfig {
    @Value("${backend.server.host}")
    private String host;

    @Value("${backend.server.port}")
    private int port;
}
