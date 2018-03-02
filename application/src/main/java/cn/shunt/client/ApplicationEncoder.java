package cn.shunt.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

class ApplicationEncoder extends MessageToByteEncoder {

    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf out) throws Exception {
        ApplicationMsg msg = (ApplicationMsg) o;
        int msgId = msg.msgId;
        byte[] bytes = null;
        switch (msgId) {
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
        int byteLen = bytes.length;
        byte[] data = new byte[byteLen + 4];
        byte[] len = intToBytes(byteLen);
        System.arraycopy(len, 0, data, 0, 4);
        System.arraycopy(bytes, 0, data, byteLen, byteLen);
        out.writeBytes(bytes);
    }

    byte[] intToBytes(int num) {
        byte[] byteNum = new byte[4];
        for (int ix = 0; ix < 4; ++ix) {
            int offset = 32 - (ix + 1) * 4;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }
}
