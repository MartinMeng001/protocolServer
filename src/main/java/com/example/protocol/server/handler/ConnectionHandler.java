package com.example.protocol.server.handler;

import com.example.protocol.server.manager.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private final ConnectionManager connectionManager;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String connectionId = ctx.channel().id().asShortText();
        connectionManager.addConnection(connectionId, ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String connectionId = ctx.channel().id().asShortText();
        connectionManager.removeConnection(connectionId);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Connection error: {}", cause.getMessage(), cause);
        ctx.close();
    }
}
