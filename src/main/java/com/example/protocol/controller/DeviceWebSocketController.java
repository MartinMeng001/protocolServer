package com.example.protocol.controller;

import com.example.protocol.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DeviceWebSocketController extends TextWebSocketHandler {

    private final DeviceService deviceService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("WebSocket连接建立: {}", session.getId());

        // 发送当前所有设备状态
        try {
            String devicesJson = objectMapper.writeValueAsString(deviceService.getAllDevices());
            session.sendMessage(new TextMessage(devicesJson));
        } catch (Exception e) {
            log.error("发送设备状态失败", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("WebSocket连接关闭: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: {}", session.getId(), exception);
        sessions.remove(session.getId());
    }

    /**
     * 广播设备状态更新到所有WebSocket客户端
     */
    public void broadcastDeviceUpdate(Object deviceData) {
        String message;
        try {
            message = objectMapper.writeValueAsString(deviceData);
        } catch (Exception e) {
            log.error("序列化设备数据失败", e);
            return;
        }

        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    log.error("发送WebSocket消息失败", e);
                }
            }
        });
    }
}
