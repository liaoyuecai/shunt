package cn.shunt.server;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

class ShuntServer extends Server {
    void init() {
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel channel)
                    throws Exception {
                channel.pipeline().addLast()
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                        .addLast(new ShuntDecoder())
                        .addLast(new ReadTimeoutHandler(60 * 1000))
                        .addLast(new ShuntEncoder())
                        .addLast(new ShuntMessageHandler());
            }
        };
        start(channelInitializer, ParamsLoader.shuntPort);
    }
}