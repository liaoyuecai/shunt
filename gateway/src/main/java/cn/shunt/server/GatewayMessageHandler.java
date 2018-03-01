package cn.shunt.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

class GatewayMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MsgLoadBalancing.connect(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MsgLoadBalancing.disConnect(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GatewayData data = (GatewayData) msg;
        MsgLoadBalancing.writeToApplicationService(data, ctx);
    }
}
