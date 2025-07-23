package com.example.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private boolean success;
    private String message;
    private Object data;

    public static MessageResponse success(String message) {
        return new MessageResponse(true, message, null);
    }

    public static MessageResponse success(String message, Object data) {
        return new MessageResponse(true, message, data);
    }

    public static MessageResponse error(String message) {
        return new MessageResponse(false, message, null);
    }
}
