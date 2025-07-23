package com.example.protocol.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "netty")
@Data
public class NettyConfig {
    private int port = 8080;
    private int bossThreads = 1;
    private int workerThreads = 0; // 0 表示使用默认值
    private int soBacklog = 128;
    private boolean keepAlive = true;
    private boolean tcpNoDelay = true;
    private int readTimeout = 60; // 秒
    private int writeTimeout = 60; // 秒
    private int maxFrameLength = 1048576; // 1MB
}
