package com.example.protocol.service;

import com.example.protocol.protocol.model.ProtocolMessage;
import com.example.protocol.protocol.util.ByteUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    private final ResponseService responseService;

    public void handleMessage(ProtocolMessage message, ChannelHandlerContext ctx) {
        try {
            // 记录接收到的消息
            String dataStr = new String(message.getData(), StandardCharsets.UTF_8);
            log.info("Received message from {}: {}",
                    ctx.channel().remoteAddress(), dataStr);

            // 处理业务逻辑
            String response = processMessage(dataStr, message.getConnectionId());

            // 发送响应
            if (response != null) {
                responseService.sendResponse(ctx, response);
            }

        } catch (Exception e) {
            log.error("Error processing message", e);
            responseService.sendErrorResponse(ctx, "Processing error: " + e.getMessage());
        }
    }

    private String processMessage(String data, String connectionId) {
        // 这里可以根据实际业务需求进行处理
        // 示例：简单的回显服务
        if (data.startsWith("PING")) {
            return "PONG";
        } else if (data.startsWith("ECHO:")) {
            return "ECHO_RESPONSE:" + data.substring(5);
        } else if (data.startsWith("TIME")) {
            return "SERVER_TIME:" + System.currentTimeMillis();
        } else if (data.startsWith("INFO")) {
            return "CONNECTION_ID:" + connectionId;
        }

        // 默认响应
        return "RECEIVED:" + data;
    }
}
