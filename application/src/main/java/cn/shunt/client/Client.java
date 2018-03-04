package cn.shunt.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

abstract class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    final String host;
    final int port;

    ChannelFuture channelFuture;

    Channel channel;

    final Bootstrap client = new Bootstrap();

    Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    abstract Client init() throws InterruptedException;

    void start(final ChannelInitializer channelInitializer) throws InterruptedException {
        final EventLoopGroup group = new NioEventLoopGroup((Runtime.getRuntime().availableProcessors() / 3));
        client.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(channelInitializer);
        Executors.newSingleThreadExecutor().submit(new Callable<Object>() {
            public Object call() throws Exception {
                channelFuture = client.connect(host, port).sync();
                connect();
                return null;
            }
        });
    }

    void connect() throws InterruptedException {
        try {
            channelFuture.channel().closeFuture().sync();
            channel = channelFuture.channel();
        } catch (Exception e) {
            logger.error("Client exception,client will reconnect in 5 seconds", e);
            TimeUnit.MILLISECONDS.sleep(5000);
        } finally {
            logger.info("client reconnect");
            connect();
        }
    }
}
