package com.example.protocol;

import com.example.protocol.server.TcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ProtocolServerApplication implements CommandLineRunner {

    @Autowired
    private TcpServer tcpServer;

    public static void main(String[] args) {
        log.info("启动物联网协议服务器...");
        SpringApplication.run(ProtocolServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            tcpServer.start();
            log.info("物联网协议服务器启动成功");
            log.info("TCP端口: 10003");
            log.info("HTTP端口: 8081");
            log.info("WebSocket端点: ws://localhost:8081/ws/device");
            log.info("API文档: http://localhost:8081/api/doc/info");
            log.info("健康检查: http://localhost:8081/api/health");
        } catch (Exception e) {
            log.error("服务器启动失败", e);
            System.exit(1);
        }
    }
}