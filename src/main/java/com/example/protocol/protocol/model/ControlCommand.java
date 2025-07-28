package com.example.protocol.protocol.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ControlCommand {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("CMD")
    private String cmd;

    @JsonProperty("Flag")
    private String flag; // A-清扫车, C-接驳车

    @JsonProperty("key")
    private Integer key; // 1-前进清扫, 2-返回清扫, 3-停止, 4-急停

    @JsonProperty("Code")
    private String code; // 重启命令密码

    @JsonProperty("mode")
    private Integer mode; // 工作模式

    // TCP配置相关
    @JsonProperty("TCPIP")
    private String tcpip;

    @JsonProperty("port")
    private String port;

    // 时间配置相关
    @JsonProperty("week")
    private Integer week;

    @JsonProperty("H")
    private Integer h;

    @JsonProperty("M")
    private Integer m;

    @JsonProperty("dolly")
    private Integer dolly;

    @JsonProperty("H1")
    private Integer h1;

    @JsonProperty("M1")
    private Integer m1;

    @JsonProperty("dolly1")
    private Integer dolly1;

    @JsonProperty("H2")
    private Integer h2;

    @JsonProperty("M2")
    private Integer m2;

    @JsonProperty("dolly2")
    private Integer dolly2;

    // 电机参数配置
    @JsonProperty("Vcall")
    private Double vcall; // 报警电压

    @JsonProperty("Cnone")
    private Double cnone; // 空转电流

    @JsonProperty("Cover")
    private Double cover; // 过载电流
}
