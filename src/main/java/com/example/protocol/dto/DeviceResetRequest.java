package com.example.protocol.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class DeviceResetRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @Pattern(regexp = "^[AC]$", message = "Flag必须为A(清扫车)或C(摆渡车)")
    private String flag;

    @NotBlank(message = "重启密码不能为空")
    private String code = "111111";
}
