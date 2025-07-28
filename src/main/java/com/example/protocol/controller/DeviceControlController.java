package com.example.protocol.controller;

import com.example.protocol.dto.*;
import com.example.protocol.service.DeviceControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/device/control")
@RequiredArgsConstructor
@Slf4j
public class DeviceControlController {

    private final DeviceControlService deviceControlService;

    /**
     * 设备重启
     */
    @PostMapping("/reset")
    public ResponseEntity<MessageResponse> resetDevice(@Valid @RequestBody DeviceResetRequest request) {
        try {
            boolean success = deviceControlService.resetDevice(
                    request.getDeviceId(),
                    request.getFlag(),
                    request.getCode()
            );

            if (success) {
                return ResponseEntity.ok(MessageResponse.success("设备重启指令已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("设备重启失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("设备重启失败: " + e.getMessage()));
        }
    }

    /**
     * 设备控制（启动/停止/急停等）
     */
    @PostMapping("/command")
    public ResponseEntity<MessageResponse> controlDevice(@Valid @RequestBody DeviceControlRequest request) {
        try {
            boolean success = deviceControlService.controlDevice(
                    request.getDeviceId(),
                    request.getFlag(),
                    request.getKey()
            );

            if (success) {
                String operation = getOperationName(request.getKey());
                return ResponseEntity.ok(MessageResponse.success("设备" + operation + "指令已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("设备控制失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("设备控制失败: " + e.getMessage()));
        }
    }

    /**
     * 设置工作模式
     */
    @PostMapping("/mode")
    public ResponseEntity<MessageResponse> setWorkMode(@Valid @RequestBody DeviceModeRequest request) {
        try {
            boolean success = deviceControlService.setWorkMode(
                    request.getDeviceId(),
                    request.getMode()
            );

            if (success) {
                String modeName = getModeName(request.getMode());
                return ResponseEntity.ok(MessageResponse.success("设备工作模式已设置为: " + modeName));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("设置工作模式失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("设置工作模式失败: " + e.getMessage()));
        }
    }

    private String getOperationName(Integer key) {
        switch (key) {
            case 1: return "前进清扫";
            case 2: return "返回清扫";
            case 3: return "停止";
            case 4: return "急停";
            default: return "未知操作";
        }
    }

    private String getModeName(Integer mode) {
        switch (mode) {
            case 0: return "自主模式";
            case 1: return "本地手动模式";
            case 2: return "测试模式";
            default: return "未知模式";
        }
    }
}
