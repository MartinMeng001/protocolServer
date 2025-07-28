package com.example.protocol.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.DecimalMax;

@Data
public class MotorConfigRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Pattern(regexp = "^[AC]$", message = "Flag必须为A(清扫车)或C(摆渡车)")
    private String flag;

    @DecimalMin(value = "0.0", message = "报警电压不能小于0")
    @DecimalMax(value = "30.0", message = "报警电压不能大于30")
    private Double vcall; // 报警电压

    @DecimalMin(value = "0.0", message = "空转电流不能小于0")
    @DecimalMax(value = "20.0", message = "空转电流不能大于20")
    private Double cnone; // 空转电流

    @DecimalMin(value = "0.0", message = "过载电流不能小于0")
    @DecimalMax(value = "50.0", message = "过载电流不能大于50")
    private Double cover; // 过载电流
}
