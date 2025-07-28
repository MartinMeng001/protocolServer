package com.example.protocol.controller;

import com.example.protocol.dto.*;
import com.example.protocol.protocol.model.DeviceData;
import com.example.protocol.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * 获取所有在线设备列表
     */
    @GetMapping("/list")
    public ResponseEntity<MessageResponse> getDeviceList() {
        try {
            List<DeviceInfo> devices = deviceService.getAllDevices();
            return ResponseEntity.ok(MessageResponse.success("获取设备列表成功", devices));
        } catch (Exception e) {
            log.error("获取设备列表失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取设备列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取设备详细信息
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<MessageResponse> getDeviceInfo(@PathVariable String deviceId) {
        try {
            DeviceData deviceData = deviceService.getDeviceData(deviceId);
            if (deviceData != null) {
                return ResponseEntity.ok(MessageResponse.success("获取设备信息成功", deviceData));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不存在或离线"));
            }
        } catch (Exception e) {
            log.error("获取设备信息失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("获取设备信息失败: " + e.getMessage()));
        }
    }

    /**
     * 查询设备系统状态
     */
    @PostMapping("/{deviceId}/system/status")
    public ResponseEntity<MessageResponse> querySystemStatus(@PathVariable String deviceId) {
        try {
            boolean success = deviceService.querySystemStatus(deviceId);
            if (success) {
                return ResponseEntity.ok(MessageResponse.success("查询系统状态指令已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("查询系统状态失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("查询系统状态失败: " + e.getMessage()));
        }
    }

    /**
     * 查询设备系统设置
     */
    @PostMapping("/{deviceId}/system/settings")
    public ResponseEntity<MessageResponse> querySystemSettings(@PathVariable String deviceId) {
        try {
            boolean success = deviceService.querySystemSettings(deviceId);
            if (success) {
                return ResponseEntity.ok(MessageResponse.success("查询系统设置指令已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("查询系统设置失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("查询系统设置失败: " + e.getMessage()));
        }
    }

    /**
     * 查询设备时间
     */
    @PostMapping("/{deviceId}/system/time")
    public ResponseEntity<MessageResponse> querySystemTime(@PathVariable String deviceId) {
        try {
            boolean success = deviceService.querySystemTime(deviceId);
            if (success) {
                return ResponseEntity.ok(MessageResponse.success("查询系统时间指令已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("查询系统时间失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("查询系统时间失败: " + e.getMessage()));
        }
    }
}
