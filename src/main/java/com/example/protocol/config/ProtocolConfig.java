package com.example.protocol.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "protocol")
@Data
public class ProtocolConfig {
    private int maxMessageLength = 65535;
    private boolean enableCrcCheck = true;
    private int bufferSize = 4096;
}
