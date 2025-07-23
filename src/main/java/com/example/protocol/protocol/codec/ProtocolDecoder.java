package com.example.protocol.protocol.codec;

import com.example.protocol.protocol.exception.ProtocolException;
import com.example.protocol.protocol.model.ProtocolConstants;
import com.example.protocol.protocol.model.ProtocolMessage;
import com.example.protocol.protocol.util.ByteUtils;
import com.example.protocol.protocol.util.CRCCalculator;
import com.example.protocol.protocol.util.EscapeProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProtocolDecoder extends ByteToMessageDecoder {

    private enum DecodeState {
        FIND_START,
        READ_LENGTH,
        READ_DATA,
        VALIDATE_END
    }

    private DecodeState state = DecodeState.FIND_START;
    private int expectedLength = 0;
    private ByteBuf dataBuf;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.isReadable()) {
            switch (state) {
                case FIND_START:
                    if (findStartByte(in)) {
                        state = DecodeState.READ_LENGTH;
                    }
                    break;

                case READ_LENGTH:
                    if (readLength(in)) {
                        state = DecodeState.READ_DATA;
                        dataBuf = ctx.alloc().buffer(expectedLength);
                    }
                    break;

                case READ_DATA:
                    if (readData(in)) {
                        state = DecodeState.VALIDATE_END;
                    }
                    break;

                case VALIDATE_END:
                    if (validateEnd(in, out, ctx)) {
                        reset();
                    }
                    break;
            }

            if (!in.isReadable()) {
                break;
            }
        }
    }

    private boolean findStartByte(ByteBuf in) {
        while (in.isReadable()) {
            byte b = in.readByte();
            if (b == ProtocolConstants.START_BYTE) {
                log.debug("Found start byte");
                return true;
            }
        }
        return false;
    }

    private boolean readLength(ByteBuf in) {
        if (in.readableBytes() < ProtocolConstants.LENGTH_FIELD_SIZE) {
            return false;
        }

        byte[] lengthBytes = new byte[ProtocolConstants.LENGTH_FIELD_SIZE];
        in.readBytes(lengthBytes);
        expectedLength = ByteUtils.bytesToShort(lengthBytes, 0, true);

        log.debug("Expected length: {}", expectedLength);

        if (expectedLength <= 0 || expectedLength > 65535) {
            log.warn("Invalid length: {}", expectedLength);
            reset();
            return false;
        }

        return true;
    }

    private boolean readData(ByteBuf in) {
        while (in.isReadable() && dataBuf.readableBytes() < expectedLength) {
            byte b = in.readByte();

            if (b == ProtocolConstants.ESCAPE_BYTE) {
                if (!in.isReadable()) {
                    // 需要等待更多数据
                    in.readerIndex(in.readerIndex() - 1);
                    return false;
                }
                byte nextByte = in.readByte();
                dataBuf.writeByte(nextByte);
            } else {
                dataBuf.writeByte(b);
            }
        }

        return dataBuf.readableBytes() >= expectedLength;
    }

    private boolean validateEnd(ByteBuf in, List<Object> out, ChannelHandlerContext ctx) {
        if (!in.isReadable()) {
            return false;
        }

        byte endByte = in.readByte();
        if (endByte != ProtocolConstants.END_BYTE) {
            log.warn("Invalid end byte: 0x{}", String.format("%02X", endByte));
            reset();
            return false;
        }

        // 提取数据和CRC
        byte[] fullData = new byte[dataBuf.readableBytes()];
        dataBuf.readBytes(fullData);

        if (fullData.length < ProtocolConstants.CRC_FIELD_SIZE) {
            log.warn("Data too short for CRC");
            reset();
            return false;
        }

        int dataLength = fullData.length - ProtocolConstants.CRC_FIELD_SIZE;
        byte[] messageData = new byte[dataLength];
        byte[] crcBytes = new byte[ProtocolConstants.CRC_FIELD_SIZE];

        System.arraycopy(fullData, 0, messageData, 0, dataLength);
        System.arraycopy(fullData, dataLength, crcBytes, 0, ProtocolConstants.CRC_FIELD_SIZE);

        // 验证CRC
        long calculatedCrc = CRCCalculator.calculateCRC32(messageData);
        long receivedCrc = ByteUtils.bytesToInt(crcBytes, 0, true) & 0xFFFFFFFFL;

        if (calculatedCrc != receivedCrc) {
            log.warn("CRC mismatch. Calculated: 0x{}, Received: 0x{}",
                    Long.toHexString(calculatedCrc), Long.toHexString(receivedCrc));
            reset();
            return false;
        }

        // 创建协议消息
        ProtocolMessage message = new ProtocolMessage(messageData);
        message.setCrc(receivedCrc);
        message.setConnectionId(ctx.channel().id().asShortText());

        out.add(message);
        log.debug("Successfully decoded message with {} bytes", messageData.length);

        return true;
    }

    private void reset() {
        state = DecodeState.FIND_START;
        expectedLength = 0;
        if (dataBuf != null) {
            dataBuf.release();
            dataBuf = null;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        reset();
        super.channelInactive(ctx);
    }
}
