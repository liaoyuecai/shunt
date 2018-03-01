package cn.shunt.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

class GatewayEncoder extends MessageToByteEncoder {

    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf out) throws Exception {
        byte[] bytes = (byte[]) o;
        out.writeBytes(bytes);
    }
}
