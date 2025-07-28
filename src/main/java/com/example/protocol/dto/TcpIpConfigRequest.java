package com.example.protocol.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class TcpIpConfigRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    @NotBlank(message = "TCP/IP地址不能为空")
    private String tcpip;

    @Pattern(regexp = "^\\d{1,5}$", message = "端口号格式不正确")
    private String port;
}
