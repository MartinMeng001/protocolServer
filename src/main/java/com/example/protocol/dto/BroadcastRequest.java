package com.example.protocol.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BroadcastRequest {

    @NotBlank(message = "Message cannot be blank")
    private String message;
}