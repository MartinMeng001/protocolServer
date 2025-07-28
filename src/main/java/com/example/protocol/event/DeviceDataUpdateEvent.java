package com.example.protocol.event;

import com.example.protocol.protocol.model.DeviceData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeviceDataUpdateEvent extends ApplicationEvent {
    private final DeviceData deviceData;

    public DeviceDataUpdateEvent(Object source, DeviceData deviceData) {
        super(source);
        this.deviceData = deviceData;
    }
}
