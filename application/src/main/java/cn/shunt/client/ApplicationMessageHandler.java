package cn.shunt.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ApplicationMessageHandler extends ChannelInboundHandlerAdapter {

    long number;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ApplicationMsg message = (ApplicationMsg) msg;
        int msgId = message.msgId;
        ApplicationMsg ack = null;
        switch (msgId) {
            case ApplicationMsg.REGISTER_ACK:
                number = message.number;
                ack = new ApplicationMsg(ApplicationMsg.REGISTER_FINISH, number);
                break;
            case ApplicationMsg.CONNECTION:

                break;
        }
        if (ack != null) {
            ctx.channel().writeAndFlush(ack);
        }
    }
}
