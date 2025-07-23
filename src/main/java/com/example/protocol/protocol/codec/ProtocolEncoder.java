package com.example.protocol.protocol.codec;

import com.example.protocol.protocol.model.ProtocolConstants;
import com.example.protocol.protocol.model.ProtocolMessage;
import com.example.protocol.protocol.util.ByteUtils;
import com.example.protocol.protocol.util.CRCCalculator;
import com.example.protocol.protocol.util.EscapeProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtocolEncoder extends MessageToByteEncoder<ProtocolMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtocolMessage msg, ByteBuf out) throws Exception {
        byte[] data = msg.getData();

        // 计算CRC
        long crc = CRCCalculator.calculateCRC32(data);
        byte[] crcBytes = ByteUtils.intToBytes((int)crc, true);

        // 组合数据和CRC
        byte[] payload = new byte[data.length + crcBytes.length];
        System.arraycopy(data, 0, payload, 0, data.length);
        System.arraycopy(crcBytes, 0, payload, data.length, crcBytes.length);

        // 转义处理
        byte[] escapedPayload = EscapeProcessor.escape(payload);

        // 构建完整报文
        out.writeByte(ProtocolConstants.START_BYTE);

        // 写入长度字段（转义前的长度）
        byte[] lengthBytes = ByteUtils.shortToBytes(payload.length, true);
        out.writeBytes(lengthBytes);

        // 写入转义后的数据
        out.writeBytes(escapedPayload);

        // 写入结束字节
        out.writeByte(ProtocolConstants.END_BYTE);

        log.debug("Encoded message: {} bytes -> {} bytes", data.length, out.readableBytes());
    }
}
