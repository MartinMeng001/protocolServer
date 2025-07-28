package com.example.protocol.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
public class DeviceControlRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Pattern(regexp = "^[AC]$", message = "Flag必须为A(清扫车)或C(摆渡车)")
    private String flag;

    @Min(value = 1, message = "操作键值最小为1")
    @Max(value = 4, message = "操作键值最大为4")
    private Integer key; // 1-前进清扫, 2-返回清扫, 3-停止, 4-急停
}
