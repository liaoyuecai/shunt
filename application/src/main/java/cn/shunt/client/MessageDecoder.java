package cn.shunt.client;

import io.netty.buffer.ByteBuf;

public interface MessageDecoder {
    Object decode(ByteBuf buf);
}
