package com.example.protocol.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceControlService {

    private final DeviceService deviceService;
    private final ProtocolService protocolService;

    /**
     * 重启设备
     */
    public boolean resetDevice(String deviceId, String flag, String code) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"Reset\",\"Code\":\"%s\",\"Flag\":\"%s\"}",
                    deviceId, code, flag
            );

            log.info("发送重启命令到设备 {}: Flag={}", deviceId, flag);
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("重启设备失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 控制设备
     */
    public boolean controlDevice(String deviceId, String flag, Integer key) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"Control\",\"Flag\":\"%s\",\"key\":%d}",
                    deviceId, flag, key
            );

            log.info("发送控制命令到设备 {}: Flag={}, Key={}", deviceId, flag, key);
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("控制设备失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 设置工作模式
     */
    public boolean setWorkMode(String deviceId, Integer mode) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"Mode\",\"mode\":%d}",
                    deviceId, mode
            );

            log.info("设置设备 {} 工作模式: {}", deviceId, mode);
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("设置工作模式失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 发送命令到设备
     */
    private boolean sendCommandToDevice(String deviceId, String command) {
        // 这里需要根据设备ID找到对应的连接ID
        String connectionId = findConnectionIdByDeviceId(deviceId);
        if (connectionId != null) {
            return protocolService.sendMessage(connectionId, command);
        }
        return false;
    }

    /**
     * 根据设备ID查找连接ID
     */
    private String findConnectionIdByDeviceId(String deviceId) {
        // 从设备服务中获取设备数据来找到连接ID
        var deviceData = deviceService.getDeviceData(deviceId);
        if (deviceData != null) {
            // 这里需要一个映射来找到设备ID对应的连接ID
            // 简化实现：遍历所有连接找到匹配的设备
            return deviceService.getAllDevices().stream()
                    .filter(info -> deviceId.equals(info.getDeviceId()))
                    .map(info -> info.getConnectionId())
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
