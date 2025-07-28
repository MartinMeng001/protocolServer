package com.example.protocol.controller;

import com.example.protocol.dto.MessageResponse;
import com.example.protocol.service.DeviceMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Slf4j
public class DeviceMonitorController {

    private final DeviceMonitorService deviceMonitorService;

    /**
     * 获取设备统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<MessageResponse> getDeviceStatistics() {
        try {
            Map<String, Object> stats = deviceMonitorService.getDeviceStatistics();
            return ResponseEntity.ok(MessageResponse.success("获取设备统计信息成功", stats));
        } catch (Exception e) {
            log.error("获取设备统计信息失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取设备统计信息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取设备告警信息
     */
    @GetMapping("/alerts")
    public ResponseEntity<MessageResponse> getDeviceAlerts() {
        try {
            Map<String, Object> alerts = deviceMonitorService.getDeviceAlerts();
            return ResponseEntity.ok(MessageResponse.success("获取设备告警信息成功", alerts));
        } catch (Exception e) {
            log.error("获取设备告警信息失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取设备告警信息失败: " + e.getMessage()));
        }
    }

    /**
     * 手动触发心跳检查
     */
    @GetMapping("/heartbeat/check")
    public ResponseEntity<MessageResponse> checkHeartbeat() {
        try {
            deviceMonitorService.checkDeviceHeartbeat();
            return ResponseEntity.ok(MessageResponse.success("心跳检查已触发"));
        } catch (Exception e) {
            log.error("心跳检查失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("心跳检查失败: " + e.getMessage()));
        }
    }
}
