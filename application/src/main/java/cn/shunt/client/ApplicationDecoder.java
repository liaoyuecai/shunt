package cn.shunt.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

class ApplicationDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
        int msgId = buf.readByte();
        ApplicationMsg msg = null;
        switch (msgId) {
            case ApplicationMsg.REGISTER_ACK:
                long number = buf.readLong();
                msg = new ApplicationMsg(msgId, number);
                break;
            case ApplicationMsg.CONNECTION:
                int addressLen = buf.readByte();
                byte[] address = new byte[addressLen];
                buf.readBytes(address);
                byte[] data = new byte[buf.readableBytes()];
                buf.readBytes(data);
                msg = new ApplicationMsg(msgId, data, new String(address, ApplicationMsg.charCode));
                break;
            default:
                msg = new ApplicationMsg(msgId);
                break;
        }
        list.add(msg);
    }
}
