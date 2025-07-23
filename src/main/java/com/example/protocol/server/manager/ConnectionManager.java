package com.example.protocol.server.manager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;

@Component
@Slf4j
public class ConnectionManager {

    private final ConcurrentMap<String, Channel> connections = new ConcurrentHashMap<>();

    public void addConnection(String connectionId, Channel channel) {
        connections.put(connectionId, channel);
        log.info("Connection added: {} ({})", connectionId, channel.remoteAddress());
    }

    public void removeConnection(String connectionId) {
        Channel channel = connections.remove(connectionId);
        if (channel != null) {
            log.info("Connection removed: {} ({})", connectionId, channel.remoteAddress());
        }
    }

    public Channel getConnection(String connectionId) {
        return connections.get(connectionId);
    }

    public Set<String> getAllConnectionIds() {
        return connections.keySet();
    }

    public int getConnectionCount() {
        return connections.size();
    }

    public boolean hasConnection(String connectionId) {
        return connections.containsKey(connectionId);
    }

    public void sendToConnection(String connectionId, Object message) {
        Channel channel = connections.get(connectionId);
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        } else {
            log.warn("Connection not found or inactive: {}", connectionId);
        }
    }

    public void broadcast(Object message) {
        connections.values().forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(message);
            }
        });
    }
}
