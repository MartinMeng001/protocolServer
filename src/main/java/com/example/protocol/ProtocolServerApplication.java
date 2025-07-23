package com.example.protocol;

import com.example.protocol.server.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProtocolServerApplication implements CommandLineRunner {

    @Autowired
    private TcpServer tcpServer;

    public static void main(String[] args) {
        SpringApplication.run(ProtocolServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        tcpServer.start();
    }
}