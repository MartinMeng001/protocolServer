package com.example.protocol.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeviceStatusChangeEvent extends ApplicationEvent {
    private final String deviceId;
    private final String status;
    private final String reason;

    public DeviceStatusChangeEvent(Object source, String deviceId, String status, String reason) {
        super(source);
        this.deviceId = deviceId;
        this.status = status;
        this.reason = reason;
    }
}
