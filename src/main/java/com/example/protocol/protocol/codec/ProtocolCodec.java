package com.example.protocol.protocol.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

public class ProtocolCodec extends CombinedChannelDuplexHandler<ProtocolDecoder, ProtocolEncoder> {

    public ProtocolCodec() {
        super(new ProtocolDecoder(), new ProtocolEncoder());
    }
}
