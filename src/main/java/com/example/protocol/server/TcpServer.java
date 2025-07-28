package com.example.protocol.server;

import com.example.protocol.config.NettyConfig;
import com.example.protocol.server.handler.ProtocolChannelInitializer;
import com.example.protocol.server.manager.ConnectionManager;
import com.example.protocol.service.MessageService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
@RequiredArgsConstructor
public class TcpServer {

    private final NettyConfig nettyConfig;
    private final ConnectionManager connectionManager;
    private final MessageService messageService;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(nettyConfig.getBossThreads());
        workerGroup = new NioEventLoopGroup(
                nettyConfig.getWorkerThreads() == 0 ?
                        Runtime.getRuntime().availableProcessors() * 2 :
                        nettyConfig.getWorkerThreads()
        );

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProtocolChannelInitializer(
                            connectionManager,
                            messageService,
                            nettyConfig.getReadTimeout(),
                            nettyConfig.getWriteTimeout()))
                    .option(ChannelOption.SO_BACKLOG, nettyConfig.getSoBacklog())
                    .childOption(ChannelOption.SO_KEEPALIVE, nettyConfig.isKeepAlive())
                    .childOption(ChannelOption.TCP_NODELAY, nettyConfig.isTcpNoDelay());

            channelFuture = bootstrap.bind(nettyConfig.getPort()).sync();
            log.info("TCP Server started on port {}", nettyConfig.getPort());

        } catch (Exception e) {
            log.error("Failed to start TCP server", e);
            shutdown();
            throw e;
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down TCP server...");

        if (channelFuture != null) {
            channelFuture.channel().close();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        log.info("TCP server shutdown completed");
    }
}
