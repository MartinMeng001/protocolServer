package com.example.protocol.service;

import com.example.protocol.protocol.model.ControlCommand;
import com.example.protocol.protocol.model.DeviceData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class JsonDataProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeviceService deviceService;

    public String processJsonData(byte[] data, String connectionId) {
        try {
            String jsonStr = new String(data, StandardCharsets.UTF_8);
            log.info("Received JSON from {}: {}", connectionId, jsonStr);

            // 解析JSON获取命令类型
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            String cmd = rootNode.path("CMD").asText();
            String id = rootNode.path("ID").asText();

            // 如果是设备状态数据，缓存到设备服务中
            if ("system".equalsIgnoreCase(cmd) || isDeviceStatusData(rootNode)) {
                cacheDeviceData(rootNode, connectionId);
            }

            // 根据命令类型处理
            switch (cmd.toLowerCase()) {
                case "system":
                case "ststem": // 处理可能的拼写错误
                    return handleSystemCommand(rootNode, connectionId);
                case "reset":
                    return handleResetCommand(rootNode, connectionId);
                case "control":
                    return handleControlCommand(rootNode, connectionId);
                case "tcpip":
                    return handleTcpipCommand(rootNode, connectionId);
                case "week":
                    return handleWeekCommand(rootNode, connectionId);
                case "mode":
                    return handleModeCommand(rootNode, connectionId);
                case "mon":
                    return handleMonCommand(rootNode, connectionId);
                default:
                    // 如果没有明确的CMD字段，但包含设备状态数据，也进行处理
                    if (isDeviceStatusData(rootNode)) {
                        return handleDeviceStatusData(rootNode, connectionId);
                    }
                    log.warn("Unknown command: {}", cmd);
                    return createErrorResponse(id, "Unknown command: " + cmd);
            }

        } catch (Exception e) {
            log.error("Error processing JSON data", e);
            return createErrorResponse("", "JSON processing error: " + e.getMessage());
        }
    }

    /**
     * 判断是否为设备状态数据
     */
    private boolean isDeviceStatusData(JsonNode node) {
        // 检查是否包含设备状态相关字段
        return node.has("W0") || node.has("W1") || node.has("W2") ||
                node.has("V") || node.has("Mode") || node.has("status");
    }

    /**
     * 缓存设备数据
     */
    private void cacheDeviceData(JsonNode node, String connectionId) {
        try {
            DeviceData deviceData = objectMapper.treeToValue(node, DeviceData.class);
            if (deviceData.getId() != null) {
                deviceService.updateDeviceData(deviceData, connectionId);
                log.debug("Cached device data for: {}", deviceData.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to cache device data", e);
        }
    }

    /**
     * 处理设备状态数据
     */
    private String handleDeviceStatusData(JsonNode node, String connectionId) {
        try {
            String id = node.path("ID").asText();
            log.info("Received device status data from: {}", id);

            // 数据已经在cacheDeviceData中缓存了，这里返回确认响应
            return String.format("{\"ID\": \"%s\", \"STATUS\": \"RECEIVED\"}", id);
        } catch (Exception e) {
            log.error("Error handling device status data", e);
            return createErrorResponse("", "Device status data error");
        }
    }

    // ... 其他处理方法保持不变 ...
    private String handleSystemCommand(JsonNode node, String connectionId) {
        try {
            String clazz = node.path("class").asText();
            String id = node.path("ID").asText();

            log.info("System command - class: {}, ID: {}", clazz, id);

            switch (clazz) {
                case "updat":
                    return createSystemStatusResponse(id);
                case "setsys":
                    return createSystemSettingsResponse(id);
                case "setlora":
                    return createLoraSettingsResponse(id);
                case "uptime":
                    return createSystemTimeResponse(id);
                default:
                    return createErrorResponse(id, "Unknown system class: " + clazz);
            }
        } catch (Exception e) {
            log.error("Error handling system command", e);
            return createErrorResponse("", "System command error");
        }
    }

    private String handleControlCommand(JsonNode node, String connectionId) {
        try {
            ControlCommand command = objectMapper.treeToValue(node, ControlCommand.class);
            log.info("Control command: key={}, flag={}", command.getKey(), command.getFlag());

            return String.format("{\"ID\": \"%s\", \"Flag\": \"%s\", \"CMD\": \"Control\"}",
                    command.getId(), command.getFlag());
        } catch (Exception e) {
            log.error("Error handling control command", e);
            return createErrorResponse("", "Control command error");
        }
    }

    private String handleResetCommand(JsonNode node, String connectionId) {
        try {
            String id = node.path("ID").asText();
            String flag = node.path("Flag").asText();

            log.info("Reset command: ID={}, Flag={}", id, flag);

            return String.format("{\"ID\": \"%s\", \"Flag\": \"%s\", \"CMD\": \"Reset\"}",
                    id, flag);
        } catch (Exception e) {
            log.error("Error handling reset command", e);
            return createErrorResponse("", "Reset command error");
        }
    }

    private String handleTcpipCommand(JsonNode node, String connectionId) {
        try {
            String id = node.path("ID").asText();
            String tcpip = node.path("TCPIP").asText();
            String port = node.path("port").asText();

            log.info("TCPIP config: ID={}, TCPIP={}, port={}", id, tcpip, port);

            return String.format("{\"ID\": \"%s\", \"Flag\": \"C\", \"CMD\": \"TCPIP\"}", id);
        } catch (Exception e) {
            log.error("Error handling TCPIP command", e);
            return createErrorResponse("", "TCPIP command error");
        }
    }

    private String handleWeekCommand(JsonNode node, String connectionId) {
        try {
            String id = node.path("ID").asText();
            log.info("Week config command for ID: {}", id);

            return String.format("{\"ID\":\"%s\",\"CMD\":\"week\",\"Flag\": \"C\"}", id);
        } catch (Exception e) {
            log.error("Error handling week command", e);
            return createErrorResponse("", "Week command error");
        }
    }

    private String handleModeCommand(JsonNode node, String connectionId) {
        try {
            String id = node.path("ID").asText();
            Integer mode = node.path("mode").asInt();

            log.info("Mode config: ID={}, mode={}", id, mode);

            return String.format("{\"ID\": \"%s\", \"Flag\": \"C\", \"CMD\": \"Mode\"}", id);
        } catch (Exception e) {
            log.error("Error handling mode command", e);
            return createErrorResponse("", "Mode command error");
        }
    }

    private String handleMonCommand(JsonNode node, String connectionId) {
        try {
            String id = node.path("ID").asText();
            String flag = node.path("Flag").asText();

            log.info("Mon config: ID={}, Flag={}", id, flag);

            return String.format("{\"ID\":\"%s\",\"CMD\":\"Mon\",\"Flag\":\"%s\"}", id, flag);
        } catch (Exception e) {
            log.error("Error handling mon command", e);
            return createErrorResponse("", "Mon command error");
        }
    }

    private String createSystemStatusResponse(String id) {
        return String.format("{\"Ms\": 2, \"VerA\": \"V2.0A\", \"D1\": 1, \"D0\": 1, " +
                "\"CLen\": \"1\", \"D2\": 1, \"mac2\": \"36\", \"C2a\": 0, " +
                "\"distance\": \"5000\", \"mac1\": \"00\", \"V0\": 22.8, " +
                "\"channel\": \"10\", \"status\": \"A\", \"ID\": \"%s\", " +
                "\"V0a\": 0, \"Mode\": 0, \"VerC\": \"V2.0C\", \"T1\": 25.6, " +
                "\"C0a\": 0, \"T2\": 25.6, \"CMD\": \"system\", \"W2\": 0, " +
                "\"C1a\": 0, \"W0\": 0, \"W1\": 0, \"lat\": \"N3650.09558\", " +
                "\"Ta\": 0, \"T0a\": 0, \"C2\": 0.1, \"C0\": 0.0, \"C1\": 0.1, " +
                "\"spd\": \"0.74\", \"V\": 23.8, \"loc\": 1, \"Va\": 0, " +
                "\"lon\": \"E11802.07853\"}", id);
    }

    private String createSystemSettingsResponse(String id) {
        return String.format("{\"Tc\": 60, \"Tguard\": 0, \"freq\": 16625, " +
                "\"Ccoef\": 0.0116, \"deadtime\": 60, \"LoraMode\": 1, " +
                "\"Mode\": 0, \"ID\": \"%s\", \"Mselect\": 2, \"PWMen\": 0}", id);
    }

    private String createLoraSettingsResponse(String id) {
        return "{\"sf\": \"5\", \"sleep\": \"2\", \"CMD\": \"LR02\", \"iq\": \"0\", " +
                "\"er\": \"63\", \"baud\": \"4\", \"stop\": \"0\", \"mode\": \"0\", " +
                "\"powe\": \"15\", \"mac2\": \"26\", \"mac1\": \"00\", " +
                "\"channel\": \"10\", \"cr\": \"2\", \"crc\": \"0\", \"level\": \"7\"}";
    }

    private String createSystemTimeResponse(String id) {
        return String.format("{\"H\": \"8\", \"year\": 2025, \"week\": 5, " +
                "\"dolly\": \"1\", \"CLen\": \"1\", \"dolly1\": \"1\", " +
                "\"hous\": 18, \"H1\": \"13\", \"M1\": \"50\", \"day\": 18, " +
                "\"M2\": \"40\", \"minu\": 43, \"sec\": 18, \"M\": \"50\", " +
                "\"mon\": 7, \"dolly2\": \"1\"}", id);
    }

    private String createErrorResponse(String id, String error) {
        return String.format("{\"ID\": \"%s\", \"ERROR\": \"%s\"}", id, error);
    }
}
