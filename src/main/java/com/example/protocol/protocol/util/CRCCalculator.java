package com.example.protocol.protocol.util;

import com.example.protocol.protocol.model.ProtocolConstants;

public class CRCCalculator {

    private static final int[] CRC_TABLE = new int[256];

    static {
        // 预计算CRC查找表
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ ProtocolConstants.CRC_POLYNOMIAL;
                } else {
                    crc = crc >>> 1;
                }
            }
            CRC_TABLE[i] = crc;
        }
    }

    public static long calculateCRC32(byte[] data) {
        return calculateCRC32(data, 0, data.length);
    }

    public static long calculateCRC32(byte[] data, int offset, int length) {
        long crc = ProtocolConstants.CRC_INITIAL_VALUE;

        for (int i = offset; i < offset + length; i++) {
            int b = data[i] & 0xFF;
            if (ProtocolConstants.CRC_INPUT_REFLECTION) {
                b = reverseBits(b);
            }
            crc = (crc >>> 8) ^ CRC_TABLE[(int)((crc ^ b) & 0xFF)];
        }

        if (ProtocolConstants.CRC_OUTPUT_REFLECTION) {
            crc = reverseBits32((int)crc);
        }

        return (crc ^ ProtocolConstants.CRC_FINAL_XOR_VALUE) & 0xFFFFFFFFL;
    }

    private static int reverseBits(int value) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result = (result << 1) | (value & 1);
            value >>>= 1;
        }
        return result;
    }

    private static int reverseBits32(int value) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (value & 1);
            value >>>= 1;
        }
        return result;
    }
}
