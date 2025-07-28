package com.example.protocol.service;

import com.example.protocol.protocol.model.ProtocolMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class ResponseService {

    public void sendResponse(ChannelHandlerContext ctx, String response) {
        try {
            byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
            ProtocolMessage responseMessage = new ProtocolMessage(responseData);
            responseMessage.setConnectionId(ctx.channel().id().asShortText());

            ctx.writeAndFlush(responseMessage);
            log.debug("Sent response to {}: {}", ctx.channel().remoteAddress(), response);

        } catch (Exception e) {
            log.error("Error sending response", e);
        }
    }

    public void sendErrorResponse(ChannelHandlerContext ctx, String error) {
        sendResponse(ctx, "ERROR:" + error);
    }
}
