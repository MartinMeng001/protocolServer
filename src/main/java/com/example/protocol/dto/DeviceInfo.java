package com.example.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String deviceId;
    private String connectionId;
    private String remoteAddress;
    private boolean online;
    private long lastHeartbeat;
    private Integer mode; // 工作模式
    private Double batteryVoltage; // 电池电压
    private String gpsStatus; // GPS状态
    private String latitude; // 纬度
    private String longitude; // 经度
}
