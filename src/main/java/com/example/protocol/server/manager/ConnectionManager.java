package com.example.protocol.server.manager;

import com.example.protocol.service.DeviceService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;

@Component
@Slf4j
public class ConnectionManager {

    private final ConcurrentMap<String, Channel> connections = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> connectionDeviceMapping = new ConcurrentHashMap<>();

    @Autowired
    @Lazy
    private DeviceService deviceService;

    public void addConnection(String connectionId, Channel channel) {
        connections.put(connectionId, channel);
        log.info("Connection added: {} ({})", connectionId, channel.remoteAddress());
    }

    public void removeConnection(String connectionId) {
        Channel channel = connections.remove(connectionId);
        if (channel != null) {
            log.info("Connection removed: {} ({})", connectionId, channel.remoteAddress());

            // 通知设备服务处理设备离线
            String deviceId = connectionDeviceMapping.remove(connectionId);
            if (deviceId != null && deviceService != null) {
                deviceService.handleDeviceOffline(deviceId);
            }
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

    /**
     * 建立连接ID与设备ID的映射关系
     */
    public void mapConnectionToDevice(String connectionId, String deviceId) {
        connectionDeviceMapping.put(connectionId, deviceId);
        log.debug("Mapped connection {} to device {}", connectionId, deviceId);
    }

    /**
     * 根据设备ID获取连接ID
     */
    public String getConnectionIdByDevice(String deviceId) {
        return connectionDeviceMapping.entrySet().stream()
                .filter(entry -> deviceId.equals(entry.getValue()))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse(null);
    }
}
