package com.example.protocol.controller;

import com.example.protocol.dto.*;
import com.example.protocol.service.DeviceConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/device/config")
@RequiredArgsConstructor
@Slf4j
public class DeviceConfigController {

    private final DeviceConfigService deviceConfigService;

    /**
     * 配置TCP/IP信息
     */
    @PostMapping("/tcpip")
    public ResponseEntity<MessageResponse> configureTcpIp(@Valid @RequestBody TcpIpConfigRequest request) {
        try {
            boolean success = deviceConfigService.configureTcpIp(
                    request.getDeviceId(),
                    request.getTcpip(),
                    request.getPort()
            );

            if (success) {
                return ResponseEntity.ok(MessageResponse.success(
                        String.format("TCP/IP配置已发送: %s:%s", request.getTcpip(), request.getPort())
                ));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("配置TCP/IP失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("配置TCP/IP失败: " + e.getMessage()));
        }
    }

    /**
     * 配置工作时间表
     */
    @PostMapping("/schedule")
    public ResponseEntity<MessageResponse> configureSchedule(@Valid @RequestBody ScheduleConfigRequest request) {
        try {
            boolean success = deviceConfigService.configureSchedule(request);

            if (success) {
                return ResponseEntity.ok(MessageResponse.success("工作时间表配置已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("配置工作时间表失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("配置工作时间表失败: " + e.getMessage()));
        }
    }

    /**
     * 查询工作时间表
     */
    @GetMapping("/{deviceId}/schedule/{week}")
    public ResponseEntity<MessageResponse> querySchedule(
            @PathVariable String deviceId,
            @PathVariable Integer week) {
        try {
            boolean success = deviceConfigService.querySchedule(deviceId, week);

            if (success) {
                return ResponseEntity.ok(MessageResponse.success("查询工作时间表指令已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("查询工作时间表失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("查询工作时间表失败: " + e.getMessage()));
        }
    }

    /**
     * 配置电机参数
     */
    @PostMapping("/motor")
    public ResponseEntity<MessageResponse> configureMotor(@Valid @RequestBody MotorConfigRequest request) {
        try {
            boolean success = deviceConfigService.configureMotor(request);

            if (success) {
                String vehicleType = "A".equals(request.getFlag()) ? "清扫车" : "摆渡车";
                return ResponseEntity.ok(MessageResponse.success(vehicleType + "电机参数配置已发送"));
            } else {
                return ResponseEntity.badRequest()
                        .body(MessageResponse.error("设备不在线或发送失败"));
            }
        } catch (Exception e) {
            log.error("配置电机参数失败", e);
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("配置电机参数失败: " + e.getMessage()));
        }
    }
}
