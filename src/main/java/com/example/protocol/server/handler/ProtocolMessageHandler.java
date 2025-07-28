package com.example.protocol.server.handler;

import com.example.protocol.protocol.model.ProtocolMessage;
import com.example.protocol.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ProtocolMessageHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private final MessageService messageService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {
        log.debug("Received message from {}: {} bytes",
                ctx.channel().remoteAddress(), msg.getData().length);

        try {
            messageService.handleMessage(msg, ctx);
        } catch (Exception e) {
            log.error("Error handling message", e);
        }
    }
}
