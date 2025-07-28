package com.example.protocol.event;

import com.example.protocol.controller.DeviceWebSocketController;
import com.example.protocol.protocol.model.DeviceData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceEventListener {

    private final DeviceWebSocketController webSocketController;

    @EventListener
    public void handleDeviceDataUpdate(DeviceDataUpdateEvent event) {
        log.debug("处理设备数据更新事件: {}", event.getDeviceData().getId());

        // 通过WebSocket推送实时数据
        webSocketController.broadcastDeviceUpdate(event.getDeviceData());
    }

    @EventListener
    public void handleDeviceStatusChange(DeviceStatusChangeEvent event) {
        log.info("设备状态变化: {} - {}", event.getDeviceId(), event.getStatus());

        // 可以在这里添加告警逻辑
        if ("OFFLINE".equals(event.getStatus())) {
            log.warn("设备离线告警: {}", event.getDeviceId());
        }
    }
}
