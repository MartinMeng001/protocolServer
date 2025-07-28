package com.example.protocol.controller;

import com.example.protocol.dto.MessageResponse;
import com.example.protocol.server.manager.ConnectionManager;
import com.example.protocol.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final ConnectionManager connectionManager;
    private final DeviceService deviceService;

    /**
     * 服务健康检查
     */
    @GetMapping
    public ResponseEntity<MessageResponse> healthCheck() {
        Map<String, Object> health = new HashMap<>();

        try {
            // 检查服务状态
            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());

            // 连接状态
            int connectionCount = connectionManager.getConnectionCount();
            health.put("activeConnections", connectionCount);

            // 设备状态
            int deviceCount = deviceService.getAllDevices().size();
            health.put("activeDevices", deviceCount);

            // JVM信息
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> jvm = new HashMap<>();
            jvm.put("totalMemory", runtime.totalMemory());
            jvm.put("freeMemory", runtime.freeMemory());
            jvm.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
            jvm.put("maxMemory", runtime.maxMemory());
            health.put("jvm", jvm);

            return ResponseEntity.ok(MessageResponse.success("服务运行正常", health));

        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(MessageResponse.error("服务异常", health));
        }
    }
}
