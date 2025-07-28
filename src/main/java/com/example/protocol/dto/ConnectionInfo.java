package com.example.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionInfo {
    private String connectionId;
    private String remoteAddress;
    private boolean active;
    private long connectedTime;
}
