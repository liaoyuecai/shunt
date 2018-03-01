package cn.shunt.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class ShuntMessageHandler extends ChannelInboundHandlerAdapter {

    long number = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MsgLoadBalancing.disRegister(number);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ApplicationMsg message = (ApplicationMsg) msg;
        int msgId = message.msgId;
        ApplicationMsg ack = null;
        switch (msgId) {
            case ApplicationMsg.REGISTER:
                if (number == 0) {
                    number = System.currentTimeMillis();
                    ack = new ApplicationMsg(ApplicationMsg.REGISTER_ACK, number);
                }
                break;
            case ApplicationMsg.REGISTER_FINISH:
                if (number == message.number)
                    MsgLoadBalancing.register(new ApplicationService(number, ctx.channel()));
                break;
            case ApplicationMsg.CONNECTION:
                MsgLoadBalancing.writeOut(new GatewayData(message.address, message.data));
                break;
        }
        if (ack != null) {
            ctx.channel().writeAndFlush(ack);
        }
    }
}
