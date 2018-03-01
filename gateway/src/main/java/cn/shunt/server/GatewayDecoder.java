package cn.shunt.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

class GatewayDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        list.add(new GatewayData(ctx.channel().remoteAddress().toString(), bytes));
    }
}
