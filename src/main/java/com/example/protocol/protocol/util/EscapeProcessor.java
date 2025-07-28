package com.example.protocol.protocol.util;

import com.example.protocol.protocol.model.ProtocolConstants;
import java.util.ArrayList;
import java.util.List;

public class EscapeProcessor {

    public static byte[] escape(byte[] data) {
        List<Byte> escaped = new ArrayList<>();

        for (byte b : data) {
            if (b == ProtocolConstants.START_BYTE ||
                    b == ProtocolConstants.END_BYTE ||
                    b == ProtocolConstants.ESCAPE_BYTE) {
                escaped.add(ProtocolConstants.ESCAPE_BYTE);
                escaped.add(b);
            } else {
                escaped.add(b);
            }
        }

        byte[] result = new byte[escaped.size()];
        for (int i = 0; i < escaped.size(); i++) {
            result[i] = escaped.get(i);
        }
        return result;
    }

    public static byte[] unescape(byte[] data) {
        List<Byte> unescaped = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            if (data[i] == ProtocolConstants.ESCAPE_BYTE && i + 1 < data.length) {
                unescaped.add(data[i + 1]);
                i++; // 跳过下一个字节
            } else {
                unescaped.add(data[i]);
            }
        }

        byte[] result = new byte[unescaped.size()];
        for (int i = 0; i < unescaped.size(); i++) {
            result[i] = unescaped.get(i);
        }
        return result;
    }

    public static int getEscapedLength(byte[] data) {
        int count = 0;
        for (byte b : data) {
            if (b == ProtocolConstants.START_BYTE ||
                    b == ProtocolConstants.END_BYTE ||
                    b == ProtocolConstants.ESCAPE_BYTE) {
                count += 2;
            } else {
                count += 1;
            }
        }
        return count;
    }
}
