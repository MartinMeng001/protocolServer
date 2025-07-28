package com.example.protocol.protocol.util;

public class ByteUtils {

    public static int bytesToInt(byte[] bytes, int offset, boolean bigEndian) {
        if (bigEndian) {
            return ((bytes[offset] & 0xFF) << 24) |
                    ((bytes[offset + 1] & 0xFF) << 16) |
                    ((bytes[offset + 2] & 0xFF) << 8) |
                    (bytes[offset + 3] & 0xFF);
        } else {
            return (bytes[offset] & 0xFF) |
                    ((bytes[offset + 1] & 0xFF) << 8) |
                    ((bytes[offset + 2] & 0xFF) << 16) |
                    ((bytes[offset + 3] & 0xFF) << 24);
        }
    }

    public static int bytesToShort(byte[] bytes, int offset, boolean bigEndian) {
        if (bigEndian) {
            return ((bytes[offset] & 0xFF) << 8) | (bytes[offset + 1] & 0xFF);
        } else {
            return (bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8);
        }
    }

    public static byte[] intToBytes(int value, boolean bigEndian) {
        byte[] bytes = new byte[4];
        if (bigEndian) {
            bytes[0] = (byte) ((value >> 24) & 0xFF);
            bytes[1] = (byte) ((value >> 16) & 0xFF);
            bytes[2] = (byte) ((value >> 8) & 0xFF);
            bytes[3] = (byte) (value & 0xFF);
        } else {
            bytes[0] = (byte) (value & 0xFF);
            bytes[1] = (byte) ((value >> 8) & 0xFF);
            bytes[2] = (byte) ((value >> 16) & 0xFF);
            bytes[3] = (byte) ((value >> 24) & 0xFF);
        }
        return bytes;
    }

    public static byte[] shortToBytes(int value, boolean bigEndian) {
        byte[] bytes = new byte[2];
        if (bigEndian) {
            bytes[0] = (byte) ((value >> 8) & 0xFF);
            bytes[1] = (byte) (value & 0xFF);
        } else {
            bytes[0] = (byte) (value & 0xFF);
            bytes[1] = (byte) ((value >> 8) & 0xFF);
        }
        return bytes;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
