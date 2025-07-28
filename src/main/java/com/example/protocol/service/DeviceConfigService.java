package com.example.protocol.service;

import com.example.protocol.dto.MotorConfigRequest;
import com.example.protocol.dto.ScheduleConfigRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceConfigService {

    private final DeviceService deviceService;
    private final ProtocolService protocolService;

    /**
     * 配置TCP/IP信息
     */
    public boolean configureTcpIp(String deviceId, String tcpip, String port) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"TCPIP\",\"TCPIP\":\"%s\",\"port\":\"%s\"}",
                    deviceId, tcpip, port
            );

            log.info("配置设备 {} TCP/IP: {}:{}", deviceId, tcpip, port);
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("配置TCP/IP失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 配置工作时间表
     */
    public boolean configureSchedule(ScheduleConfigRequest request) {
        try {
            StringBuilder commandBuilder = new StringBuilder();
            commandBuilder.append(String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"Week\",\"week\":%d,\"H\":%d,\"M\":%d,\"dolly\":%d",
                    request.getDeviceId(), request.getWeek(),
                    request.getH(), request.getM(), request.getDolly()
            ));

            // 添加第二时段
            if (request.getH1() != null && request.getM1() != null && request.getDolly1() != null) {
                commandBuilder.append(String.format(
                        ",\"H1\":%d,\"M1\":%d,\"dolly1\":%d",
                        request.getH1(), request.getM1(), request.getDolly1()
                ));
            }

            // 添加第三时段
            if (request.getH2() != null && request.getM2() != null && request.getDolly2() != null) {
                commandBuilder.append(String.format(
                        ",\"H2\":%d,\"M2\":%d,\"dolly2\":%d",
                        request.getH2(), request.getM2(), request.getDolly2()
                ));
            }

            commandBuilder.append(",\"class\":\"wr\"}");
            String command = commandBuilder.toString();

            log.info("配置设备 {} 工作时间表: 星期{}", request.getDeviceId(), request.getWeek());
            return sendCommandToDevice(request.getDeviceId(), command);
        } catch (Exception e) {
            log.error("配置工作时间表失败: {}", request.getDeviceId(), e);
            return false;
        }
    }

    /**
     * 查询工作时间表
     */
    public boolean querySchedule(String deviceId, Integer week) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"Week\",\"week\":%d,\"class\":\"rd\"}",
                    deviceId, week
            );

            log.info("查询设备 {} 星期{} 工作时间表", deviceId, week);
            return sendCommandToDevice(deviceId, command);
        } catch (Exception e) {
            log.error("查询工作时间表失败: {}", deviceId, e);
            return false;
        }
    }

    /**
     * 配置电机参数
     */
    public boolean configureMotor(MotorConfigRequest request) {
        try {
            String command = String.format(
                    "{\"ID\":\"%s\",\"CMD\":\"Mon\",\"Vcall\":%.1f,\"Cnone\":%.1f,\"Cover\":%.1f,\"Flag\":\"%s\"}",
                    request.getDeviceId(), request.getVcall(),
                    request.getCnone(), request.getCover(), request.getFlag()
            );

            String vehicleType = "A".equals(request.getFlag()) ? "清扫车" : "摆渡车";
            log.info("配置设备 {} {} 电机参数: 报警电压={}, 空转电流={}, 过载电流={}",
                    request.getDeviceId(), vehicleType,
                    request.getVcall(), request.getCnone(), request.getCover());

            return sendCommandToDevice(request.getDeviceId(), command);
        } catch (Exception e) {
            log.error("配置电机参数失败: {}", request.getDeviceId(), e);
            return false;
        }
    }

    /**
     * 发送命令到设备
     */
    private boolean sendCommandToDevice(String deviceId, String command) {
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
        return deviceService.getAllDevices().stream()
                .filter(info -> deviceId.equals(info.getDeviceId()))
                .map(info -> info.getConnectionId())
                .findFirst()
                .orElse(null);
    }
}
