package com.example.protocol.server.handler;

import com.example.protocol.protocol.codec.ProtocolCodec;
import com.example.protocol.server.manager.ConnectionManager;
import com.example.protocol.service.MessageService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ProtocolChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ConnectionManager connectionManager;
    private final MessageService messageService;
    private final int readTimeout;
    private final int writeTimeout;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 超时处理
        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(readTimeout, TimeUnit.SECONDS));
        pipeline.addLast("writeTimeoutHandler", new WriteTimeoutHandler(writeTimeout, TimeUnit.SECONDS));

        // 协议编解码
        pipeline.addLast("protocolCodec", new ProtocolCodec());

        // 连接管理
        pipeline.addLast("connectionHandler", new ConnectionHandler(connectionManager));

        // 消息处理
        pipeline.addLast("messageHandler", new ProtocolMessageHandler(messageService));
    }
}
