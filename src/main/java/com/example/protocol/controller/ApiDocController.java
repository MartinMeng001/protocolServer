package com.example.protocol.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/doc")
public class ApiDocController {

    /**
     * 获取API文档信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();

        // 基本信息
        apiInfo.put("name", "Protocol Server API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("description", "物联网设备通信协议服务API");

        // API端点分组
        Map<String, List<String>> endpoints = new HashMap<>();
        endpoints.put("设备管理", Arrays.asList(
                "GET /api/device/list - 获取设备列表",
                "GET /api/device/{deviceId} - 获取设备详情",
                "POST /api/device/{deviceId}/system/status - 查询系统状态",
                "POST /api/device/{deviceId}/system/settings - 查询系统设置",
                "POST /api/device/{deviceId}/system/time - 查询系统时间"
        ));

        endpoints.put("设备控制", Arrays.asList(
                "POST /api/device/control/reset - 设备重启",
                "POST /api/device/control/command - 设备控制(启动/停止/急停)",
                "POST /api/device/control/mode - 设置工作模式"
        ));

        endpoints.put("设备配置", Arrays.asList(
                "POST /api/device/config/tcpip - 配置TCP/IP",
                "POST /api/device/config/schedule - 配置工作时间表",
                "GET /api/device/config/{deviceId}/schedule/{week} - 查询工作时间表",
                "POST /api/device/config/motor - 配置电机参数"
        ));

        endpoints.put("监控统计", Arrays.asList(
                "GET /api/monitor/statistics - 获取设备统计",
                "GET /api/monitor/alerts - 获取设备告警",
                "GET /api/monitor/heartbeat/check - 触发心跳检查"
        ));

        endpoints.put("协议管理", Arrays.asList(
                "POST /api/protocol/send - 发送消息",
                "POST /api/protocol/broadcast - 广播消息",
                "GET /api/protocol/connections - 获取连接列表",
                "GET /api/protocol/connections/count - 获取连接数",
                "GET /api/protocol/connections/{connectionId}/status - 获取连接状态",
                "DELETE /api/protocol/connections/{connectionId} - 关闭连接"
        ));

        apiInfo.put("endpoints", endpoints);

        // 数据模型说明
        Map<String, Object> models = new HashMap<>();
        models.put("设备工作模式", Map.of(
                "0", "自主模式",
                "1", "本地手动模式",
                "2", "测试模式"
        ));

        models.put("控制操作键值", Map.of(
                "1", "前进清扫",
                "2", "返回清扫",
                "3", "停止",
                "4", "急停"
        ));

        models.put("设备标识", Map.of(
                "A", "清扫车",
                "C", "摆渡车"
        ));

        models.put("电机工作状态", Map.of(
                "0", "停止",
                "1", "运行中",
                "2", "故障"
        ));

        models.put("电流警示状态", Map.of(
                "0", "正常",
                "1", "堵转",
                "2", "空转"
        ));

        apiInfo.put("models", models);

        return ResponseEntity.ok(apiInfo);
    }

    /**
     * 获取协议格式说明
     */
    @GetMapping("/protocol")
    public ResponseEntity<Map<String, Object>> getProtocolInfo() {
        Map<String, Object> protocolInfo = new HashMap<>();

        protocolInfo.put("name", "物联网设备通信协议");
        protocolInfo.put("version", "V1.6");
        protocolInfo.put("description", "基于TCP/IP长连接的JSON数据通信协议");

        // 协议层次
        Map<String, String> layers = new HashMap<>();
        layers.put("传输层", "TCP/IP长连接");
        layers.put("帧协议层", "自定义帧格式(0x7E开始, 0x7D结束, CRC校验)");
        layers.put("应用层", "JSON格式的业务数据");

        protocolInfo.put("layers", layers);

        // 帧格式
        Map<String, Object> frameFormat = new HashMap<>();
        frameFormat.put("开始字节", "0x7E (1字节)");
        frameFormat.put("长度字段", "大端序 (2字节)");
        frameFormat.put("数据字段", "JSON格式，经过转义处理 (变长)");
        frameFormat.put("CRC校验", "CRC-32/ISO-HDLC (4字节)");
        frameFormat.put("结束字节", "0x7D (1字节)");

        protocolInfo.put("frameFormat", frameFormat);

        // 转义规则
        Map<String, String> escapeRules = new HashMap<>();
        escapeRules.put("0x7E", "0x5C 0x7E");
        escapeRules.put("0x7D", "0x5C 0x7D");
        escapeRules.put("0x5C", "0x5C 0x5C");

        protocolInfo.put("escapeRules", escapeRules);

        return ResponseEntity.ok(protocolInfo);
    }
}
