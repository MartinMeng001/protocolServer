package com.example.protocol.protocol.model;

public class ProtocolConstants {
    // 协议特殊字节
    public static final byte START_BYTE = (byte) 0x7E;
    public static final byte END_BYTE = (byte) 0x7D;
    public static final byte ESCAPE_BYTE = (byte) 0x5C;

    // CRC-32/ISO-HDLC 参数
    public static final int CRC_POLYNOMIAL = 0x04C11DB7;
    public static final int CRC_INITIAL_VALUE = 0xFFFFFFFF;
    public static final int CRC_FINAL_XOR_VALUE = 0xFFFFFFFF;
    public static final boolean CRC_INPUT_REFLECTION = true;
    public static final boolean CRC_OUTPUT_REFLECTION = true;

    // 协议字段长度
    public static final int LENGTH_FIELD_SIZE = 2;
    public static final int CRC_FIELD_SIZE = 4;
    public static final int MIN_FRAME_SIZE = 1 + LENGTH_FIELD_SIZE + CRC_FIELD_SIZE + 1; // 8字节
}