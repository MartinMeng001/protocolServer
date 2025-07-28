package com.example.protocol.protocol.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolMessage {
    private byte[] data;
    private long crc;
    private String connectionId;
    private long timestamp;

    public ProtocolMessage(byte[] data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}
