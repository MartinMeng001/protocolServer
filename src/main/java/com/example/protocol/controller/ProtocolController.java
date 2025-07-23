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

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        log.info("API request to send message to connection: {}", request.getConnectionId());

        boolean success = protocolService.sendMessage(request.getConnectionId(), request.getMessage());

        if (success) {
            return ResponseEntity.ok(MessageResponse.success("Message sent successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(MessageResponse.error("Failed to send message. Connection not found or inactive."));
        }
    }

    @PostMapping("/broadcast")
    public ResponseEntity<MessageResponse> broadcastMessage(@Valid @RequestBody BroadcastRequest request) {
        log.info("API request to broadcast message");

        try {
            protocolService.broadcastMessage(request.getMessage());
            return ResponseEntity.ok(MessageResponse.success("Message broadcasted successfully"));
        } catch (Exception e) {
            log.error("Error broadcasting message", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("Failed to broadcast message: " + e.getMessage()));
        }
    }

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
                                    System.currentTimeMillis() // 简化实现，实际应该记录连接时间
                            );
                        }
                        return null;
                    })
                    .filter(info -> info != null)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(MessageResponse.success("Connections retrieved successfully", connections));
        } catch (Exception e) {
            log.error("Error retrieving connections", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("Failed to retrieve connections: " + e.getMessage()));
        }
    }

    @GetMapping("/connections/count")
    public ResponseEntity<MessageResponse> getConnectionCount() {
        try {
            int count = protocolService.getConnectionCount();
            Map<String, Object> data = new HashMap<>();
            data.put("count", count);

            return ResponseEntity.ok(MessageResponse.success("Connection count retrieved successfully", data));
        } catch (Exception e) {
            log.error("Error retrieving connection count", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("Failed to retrieve connection count: " + e.getMessage()));
        }
    }

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

            return ResponseEntity.ok(MessageResponse.success("Connection status retrieved successfully", status));
        } catch (Exception e) {
            log.error("Error retrieving connection status", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("Failed to retrieve connection status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/connections/{connectionId}")
    public ResponseEntity<MessageResponse> closeConnection(@PathVariable String connectionId) {
        try {
            Channel channel = connectionManager.getConnection(connectionId);
            if (channel != null) {
                channel.close();
                return ResponseEntity.ok(MessageResponse.success("Connection closed successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("Connection not found"));
            }
        } catch (Exception e) {
            log.error("Error closing connection", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("Failed to close connection: " + e.getMessage()));
        }
    }
}
