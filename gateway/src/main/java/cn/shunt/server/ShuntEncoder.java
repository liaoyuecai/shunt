package cn.shunt.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

class ShuntEncoder extends MessageToByteEncoder {

    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf out) throws Exception {
        ApplicationMsg msg = (ApplicationMsg) o;
        int msgId = msg.msgId;
        byte[] bytes = null;
        switch (msgId) {
            case ApplicationMsg.REGISTER_ACK:
                bytes = new byte[5];
                bytes[0] = (byte) msgId;
                byte[] number = longToBytes(msg.number);
                System.arraycopy(number, 0, bytes, 1, 4);
                break;
            case ApplicationMsg.CONNECTION:
                byte[] address = msg.address.getBytes(ApplicationMsg.charCode);
                bytes = new byte[address.length + msg.data.length + 2];
                bytes[0] = (byte) msgId;
                bytes[1] = (byte) address.length;
                System.arraycopy(address, 0, bytes, 1, address.length);
                System.arraycopy(msg.data, 0, bytes, 1 + address.length, msg.data.length);
                break;
            default:
                bytes = new byte[1];
                bytes[0] = (byte) msgId;
                break;
        }
        out.writeBytes(bytes);
    }

    byte[] longToBytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }
}
