package com.example.protocol.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
public class DeviceModeRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Min(value = 0, message = "模式值最小为0")
    @Max(value = 2, message = "模式值最大为2")
    private Integer mode; // 0-自主模式, 1-本地模式, 2-测试模式
}
