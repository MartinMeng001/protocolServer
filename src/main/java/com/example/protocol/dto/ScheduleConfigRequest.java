package com.example.protocol.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
public class ScheduleConfigRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Min(value = 1, message = "星期值最小为1")
    @Max(value = 7, message = "星期值最大为7")
    private Integer week; // 1-7 星期一到星期天

    @Min(value = 0, message = "小时值最小为0")
    @Max(value = 23, message = "小时值最大为23")
    private Integer h; // 第一时段小时

    @Min(value = 0, message = "分钟值最小为0")
    @Max(value = 59, message = "分钟值最大为59")
    private Integer m; // 第一时段分钟

    @Min(value = 0, message = "启动标志最小为0")
    @Max(value = 1, message = "启动标志最大为1")
    private Integer dolly; // 第一时段是否启动

    private Integer h1; // 第二时段小时
    private Integer m1; // 第二时段分钟
    private Integer dolly1; // 第二时段是否启动

    private Integer h2; // 第三时段小时
    private Integer m2; // 第三时段分钟
    private Integer dolly2; // 第三时段是否启动
}
