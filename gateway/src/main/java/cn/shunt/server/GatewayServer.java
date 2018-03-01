package cn.shunt.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

class GatewayServer extends Server {
    void init() {
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel channel)
                    throws Exception {
                channel.pipeline().addLast()
                        .addLast(new GatewayDecoder())
                        .addLast(new ReadTimeoutHandler(60 * 1000))
                        .addLast(new GatewayEncoder())
                        .addLast(new GatewayMessageHandler());
            }
        };
        start(channelInitializer, ParamsLoader.gatewayPort);
    }
}
