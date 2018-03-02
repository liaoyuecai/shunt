package cn.shunt.client;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

class ApplicationClient extends Client {
    ApplicationClient(String host, int port) {
        super(host, port);
    }

    Client init() throws InterruptedException {
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel channel)
                    throws Exception {
                channel.pipeline().addLast()
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                        .addLast(new ApplicationDecoder())
                        .addLast(new ReadTimeoutHandler(30 * 1000))
                        .addLast(new ApplicationEncoder())
                        .addLast(new ApplicationMessageHandler());
            }
        };
        start(channelInitializer);
        return this;
    }
}
