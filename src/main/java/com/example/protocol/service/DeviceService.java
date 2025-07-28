package com.example.protocol.service;

import com.example.protocol.dto.DeviceInfo;
import com.example.protocol.event.DeviceDataUpdateEvent;
import com.example.protocol.event.DeviceStatusChangeEvent;
import com.example.protocol.protocol.model.DeviceData;
import com.example.protocol.server.manager.ConnectionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {

    private final ConnectionManager connectionManager;
    private final ProtocolService protocolService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储设备数据的内存缓存
    private final Map<String, DeviceData> deviceDataCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lastHeartbeatCache = new ConcurrentHashMap<>();
    private final Map<String, String> deviceConnectionMapping = new ConcurrentHashMap<>();

    /**
     * 获取所有在线设备
     */
    public List<DeviceInfo> getAllDevices() {
        return connectionManager.getAllConnectionIds().stream()
                .map(connectionId -> {
                    DeviceData deviceData = getDeviceDataByConnectionId(connectionId);
                    return convertToDeviceInfo(connectionId, deviceData);
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据设备ID获取设备数据
     */
    public DeviceData getDeviceData(String deviceId) {
        return deviceDataCache.get(deviceId);
    }

    /**
     * 根据连接ID获取设备数据
     */
    public DeviceData getDeviceDataByConnectionId(String connectionId) {
        return deviceDataCache.values().stream()
                .filter(data -> connectionId.equals(deviceConnectionMapping.get(data.getId())))
                .findFirst()
                .orElse(null);
    }

    /**
     * 更新设备数据
     */
    public void updateDeviceData(DeviceData deviceData, String connectionId) {
        if (deviceData.getId() != null) {
            DeviceData oldData = deviceDataCache.get(deviceData.getId());

            deviceDataCache.put(deviceData.getId(), deviceData);
            lastHeartbeatCache.put(deviceData.getId(), System.currentTimeMillis());
            deviceConnectionMapping.put(deviceData.getId(), connectionId);

            log.debug("Updated device data for: {}", deviceData.getId());

            // 发布设备数据更新事件
            eventPublisher.publishEvent(new DeviceDataUpdateEvent(this, deviceData));

            // 检查设备状态变化
            if (oldData == null) {
                eventPublisher.publishEvent(new DeviceStatusChangeEvent(
                        this, deviceData.getId(), "ONLINE", "Device connected"
                ));
            } else if (!oldData.getMode().equals(deviceData.getMode())) {
                eventPublisher.publishEvent(new DeviceStatusChangeEvent(
                        this, deviceData.getId(), "MODE_CHANGED",
                        "Mode changed from " + oldData.getMode() + " to " + deviceData.getMode()
                ));
            }
        }
    }

    /**
     * 设备离线处理
     */
    public void handleDeviceOffline(String deviceId) {
        if (deviceDataCache.containsKey(deviceId)) {
            deviceConnectionMapping.remove(deviceId);
            eventPublisher.publishEvent(new DeviceStatusChangeEvent(
                    this, deviceId, "OFFLINE", "Connection lost"
            ));
        }
    }

    /**
     * 查询设备系统状态
     */
    public boolean querySystemStatus(String deviceId) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"system\",\"class\":\"updat\"}",
                    deviceId
            );
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("查询系统状态失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 查询设备系统设置
     */
    public boolean querySystemSettings(String deviceId) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"system\",\"class\":\"setsys\"}",
                    deviceId
            );
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("查询系统设置失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 查询设备系统时间
     */
    public boolean querySystemTime(String deviceId) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"system\",\"class\":\"uptime\"}",
                    deviceId
            );
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("查询系统时间失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 发送命令到设备
     */
    private boolean sendCommandToDevice(String deviceId, String command) {
        String connectionId = deviceConnectionMapping.get(deviceId);
        if (connectionId != null && connectionManager.hasConnection(connectionId)) {
            return protocolService.sendMessage(connectionId, command);
        }
        return false;
    }

    /**
     * 转换为设备信息DTO
     */
    private DeviceInfo convertToDeviceInfo(String connectionId, DeviceData deviceData) {
        DeviceInfo info = new DeviceInfo();
        info.setConnectionId(connectionId);
        info.setOnline(connectionManager.hasConnection(connectionId));

        if (deviceData != null) {
            info.setDeviceId(deviceData.getId());
            info.setMode(deviceData.getMode());
            info.setBatteryVoltage(deviceData.getV());
            info.setGpsStatus(deviceData.getStatus());
            info.setLatitude(deviceData.getLat());
            info.setLongitude(deviceData.getLon());
            info.setLastHeartbeat(lastHeartbeatCache.getOrDefault(deviceData.getId(), 0L));
        }

        // 获取远程地址
        if (connectionManager.hasConnection(connectionId)) {
            var channel = connectionManager.getConnection(connectionId);
            if (channel != null) {
                info.setRemoteAddress(channel.remoteAddress().toString());
            }
        }

        return info;
    }
}
