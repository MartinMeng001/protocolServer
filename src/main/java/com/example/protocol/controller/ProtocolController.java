package com.example.protocol.controller;

import com.example.protocol.dto.BroadcastRequest;
import com.example.protocol.dto.ConnectionInfo;
import com.example.protocol.dto.MessageRequest;
import com.example.protocol.dto.MessageResponse;
import com.example.protocol.server.manager.ConnectionManager;
import com.example.protocol.service.ProtocolService;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/protocol")
@RequiredArgsConstructor
@Slf4j
public class ProtocolController {

    private final ProtocolService protocolService;
    private final ConnectionManager connectionManager;

    /**
     * 发送消息到指定连接
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        log.info("API request to send message to connection: {}", request.getConnectionId());

        boolean success = protocolService.sendMessage(request.getConnectionId(), request.getMessage());

        if (success) {
            return ResponseEntity.ok(MessageResponse.success("消息发送成功"));
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.error("消息发送失败，连接不存在或不活跃"));
        }
    }

    /**
     * 广播消息到所有连接
     */
    @PostMapping("/broadcast")
    public ResponseEntity<MessageResponse> broadcastMessage(@Valid @RequestBody BroadcastRequest request) {
        log.info("API request to broadcast message");

        try {
            protocolService.broadcastMessage(request.getMessage());
            return ResponseEntity.ok(MessageResponse.success("消息广播成功"));
        } catch (Exception e) {
            log.error("Error broadcasting message", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("消息广播失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有连接信息
     */
    @GetMapping("/connections")
    public ResponseEntity<MessageResponse> getConnections() {
        try {
            Set<String> connectionIds = protocolService.getActiveConnections();
            List<ConnectionInfo> connections = connectionIds.stream()
                    .map(id -> {
                        Channel channel = connectionManager.getConnection(id);
                        if (channel != null) {
                            return new ConnectionInfo(
                                    id,
                                    channel.remoteAddress().toString(),
                                    channel.isActive(),
                                    System.currentTimeMillis() // 简化实现
                            );
                        }
                        return null;
                    })
                    .filter(info -> info != null)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(MessageResponse.success("连接信息获取成功", connections));
        } catch (Exception e) {
            log.error("Error retrieving connections", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取连接信息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取连接总数
     */
    @GetMapping("/connections/count")
    public ResponseEntity<MessageResponse> getConnectionCount() {
        try {
            int count = protocolService.getConnectionCount();
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);

            return ResponseEntity.ok(MessageResponse.success("连接数获取成功", data));
        } catch (Exception e) {
            log.error("Error retrieving connection count", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取连接数失败: " + e.getMessage()));
        }
    }

    /**
     * 获取指定连接状态
     */
    @GetMapping("/connections/{connectionId}/status")
    public ResponseEntity<MessageResponse> getConnectionStatus(@PathVariable String connectionId) {
        try {
            boolean exists = connectionManager.hasConnection(connectionId);
            Channel channel = connectionManager.getConnection(connectionId);

            Map<String, Object> status = new HashMap<>();
            status.put("exists", exists);
            status.put("active", channel != null && channel.isActive());

            if (channel != null) {
                status.put("remoteAddress", channel.remoteAddress().toString());
                status.put("localAddress", channel.localAddress().toString());
            }

            return ResponseEntity.ok(MessageResponse.success("连接状态获取成功", status));
        } catch (Exception e) {
            log.error("Error retrieving connection status", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取连接状态失败: " + e.getMessage()));
        }
    }

    /**
     * 关闭指定连接
     */
    @DeleteMapping("/connections/{connectionId}")
    public ResponseEntity<MessageResponse> closeConnection(@PathVariable String connectionId) {
        try {
            Channel channel = connectionManager.getConnection(connectionId);
            if (channel != null) {
                channel.close();
                return ResponseEntity.ok(MessageResponse.success("连接关闭成功"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("连接不存在"));
            }
        } catch (Exception e) {
            log.error("Error closing connection", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("关闭连接失败: " + e.getMessage()));
        }
    }
}
