package com.example.protocol.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MessageRequest {

    @NotBlank(message = "Connection ID cannot be blank")
    private String connectionId;

    @NotBlank(message = "Message cannot be blank")
    private String message;
}
