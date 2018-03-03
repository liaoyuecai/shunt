package cn.shunt.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class MessageDecoder {

    void decode(MessageChannel channel, ApplicationMsg msg) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(msg.data);
        channel.handler.receive(channel, decode(channel, buf));
    }

    abstract Object decode(MessageChannel channel, ByteBuf buf);
}
