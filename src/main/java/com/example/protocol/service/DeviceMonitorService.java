package com.example.protocol.service;

import com.example.protocol.dto.DeviceInfo;
import com.example.protocol.protocol.model.DeviceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceMonitorService {

    private final DeviceService deviceService;
    private static final long HEARTBEAT_TIMEOUT = 5 * 60 * 1000; // 5分钟超时

    /**
     * 定期检查设备心跳状态
     */
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkDeviceHeartbeat() {
        List<DeviceInfo> devices = deviceService.getAllDevices();
        long currentTime = System.currentTimeMillis();

        for (DeviceInfo device : devices) {
            if (device.getLastHeartbeat() > 0) {
                long lastHeartbeat = device.getLastHeartbeat();
                if (currentTime - lastHeartbeat > HEARTBEAT_TIMEOUT) {
                    log.warn("设备 {} 心跳超时，最后心跳时间: {}",
                            device.getDeviceId(),
                            new java.util.Date(lastHeartbeat));
                }
            }
        }
    }

    /**
     * 获取设备统计信息
     */
    public Map<String, Object> getDeviceStatistics() {
        List<DeviceInfo> devices = deviceService.getAllDevices();
        Map<String, Object> stats = new HashMap<>();

        int totalDevices = devices.size();
        int onlineDevices = (int) devices.stream().filter(DeviceInfo::isOnline).count();
        int offlineDevices = totalDevices - onlineDevices;

        // 按工作模式统计
        Map<Integer, Long> modeStats = new HashMap<>();
        devices.forEach(device -> {
            Integer mode = device.getMode();
            if (mode != null) {
                modeStats.merge(mode, 1L, Long::sum);
            }
        });

        stats.put("totalDevices", totalDevices);
        stats.put("onlineDevices", onlineDevices);
        stats.put("offlineDevices", offlineDevices);
        stats.put("modeStatistics", modeStats);
        stats.put("lastUpdateTime", System.currentTimeMillis());

        return stats;
    }

    /**
     * 获取设备告警信息
     */
    public Map<String, Object> getDeviceAlerts() {
        List<DeviceInfo> devices = deviceService.getAllDevices();
        Map<String, Object> alerts = new HashMap<>();

        // 低电压告警
        List<String> lowBatteryDevices = devices.stream()
                .filter(device -> device.getBatteryVoltage() != null && device.getBatteryVoltage() < 20.0)
                .map(DeviceInfo::getDeviceId)
                .toList();

        // GPS未定位告警
        List<String> gpsErrorDevices = devices.stream()
                .filter(device -> "V".equals(device.getGpsStatus()))
                .map(DeviceInfo::getDeviceId)
                .toList();

        alerts.put("lowBatteryDevices", lowBatteryDevices);
        alerts.put("gpsErrorDevices", gpsErrorDevices);
        alerts.put("alertTime", System.currentTimeMillis());

        return alerts;
    }
}
