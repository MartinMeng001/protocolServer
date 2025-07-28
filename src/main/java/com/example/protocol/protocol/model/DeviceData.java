package com.example.protocol.protocol.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceData {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("CMD")
    private String cmd;

    @JsonProperty("class")
    private String clazz;

    // 设备工作状态
    @JsonProperty("Mode")
    private Integer mode; // 0-自主模式，1-本地模式，2-测试模式

    // 电机工作状态
    @JsonProperty("W0")
    private Integer w0; // 摆渡车电机工作状态

    @JsonProperty("W1")
    private Integer w1; // 小车清扫电机工作状态

    @JsonProperty("W2")
    private Integer w2; // 小车行进电机工作状态

    // 电池电压
    @JsonProperty("V")
    private Double v; // 小车电池电压

    @JsonProperty("V0")
    private Double v0; // 摆渡车电池电压

    // 电机转动方向
    @JsonProperty("D0")
    private Integer d0; // 摆渡车电机转动方向

    @JsonProperty("D1")
    private Integer d1; // 小车清扫电机转动方向

    @JsonProperty("D2")
    private Integer d2; // 小车行进电机转动方向

    // 电机电流
    @JsonProperty("C0")
    private Double c0; // 摆渡车电机电流

    @JsonProperty("C1")
    private Double c1; // 小车清扫电机电流

    @JsonProperty("C2")
    private Double c2; // 小车行进电机电流

    // 电流警示
    @JsonProperty("C0a")
    private Integer c0a; // 摆渡车电机电流警示

    @JsonProperty("C1a")
    private Integer c1a; // 小车清扫电机电流警示

    @JsonProperty("C2a")
    private Integer c2a; // 小车行进电机电流警示

    // 温度
    @JsonProperty("T1")
    private Double t1;

    @JsonProperty("T2")
    private Double t2;

    @JsonProperty("Ta")
    private Integer ta; // 小车功率件温度警示

    @JsonProperty("T0a")
    private Integer t0a; // 摆渡车功率件温度警示

    // GPS相关
    @JsonProperty("status")
    private String status; // GPS定位状态

    @JsonProperty("lat")
    private String lat; // 纬度

    @JsonProperty("lon")
    private String lon; // 经度

    @JsonProperty("spd")
    private String spd; // 速度

    // 其他字段
    @JsonProperty("channel")
    private String channel;

    @JsonProperty("mac1")
    private String mac1;

    @JsonProperty("mac2")
    private String mac2;

    @JsonProperty("distance")
    private String distance;
}
