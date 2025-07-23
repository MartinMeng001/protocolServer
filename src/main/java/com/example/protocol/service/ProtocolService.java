package com.example.protocol.service;

import com.example.protocol.protocol.model.ProtocolMessage;
import com.example.protocol.server.manager.ConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProtocolService {

    private final ConnectionManager connectionManager;

    public boolean sendMessage(String connectionId, String message) {
        try {
            if (!connectionManager.hasConnection(connectionId)) {
                log.warn("Connection not found: {}", connectionId);
                return false;
            }

            byte[] messageData = message.getBytes(StandardCharsets.UTF_8);
            ProtocolMessage protocolMessage = new ProtocolMessage(messageData);
            protocolMessage.setConnectionId(connectionId);

            connectionManager.sendToConnection(connectionId, protocolMessage);
            log.info("Message sent to connection {}: {}", connectionId, message);
            return true;

        } catch (Exception e) {
            log.error("Error sending message to connection " + connectionId, e);
            return false;
        }
    }

    public void broadcastMessage(String message) {
        try {
            byte[] messageData = message.getBytes(StandardCharsets.UTF_8);
            ProtocolMessage protocolMessage = new ProtocolMessage(messageData);

            connectionManager.broadcast(protocolMessage);
            log.info("Broadcast message: {}", message);

        } catch (Exception e) {
            log.error("Error broadcasting message", e);
        }
    }

    public Set<String> getActiveConnections() {
        return connectionManager.getAllConnectionIds();
    }

    public int getConnectionCount() {
        return connectionManager.getConnectionCount();
    }
}
