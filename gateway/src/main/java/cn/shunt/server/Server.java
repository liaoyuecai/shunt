package cn.shunt.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

abstract class Server {
    static final Logger logger = LoggerFactory.getLogger(Server.class);
    final EventLoopGroup boss = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() / 3);
    final EventLoopGroup worker = new NioEventLoopGroup();
    final ServerBootstrap bootstrap = new ServerBootstrap();

    abstract Server init();

    void start(ChannelInitializer channelInitializer, final int port) {
        bootstrap.group(boss, worker).
                channel(NioServerSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(channelInitializer);
        Executors.newSingleThreadExecutor().submit(new Callable<Object>() {
            public Object call() throws Exception {
                try {
                    ChannelFuture ch = bootstrap.bind(port).sync();
                    ch.channel().closeFuture().sync();
                } catch (Exception e) {
                    throw new RuntimeException("Server start failed", e);
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                    logger.warn("Server has been over");
                }
                return null;
            }
        });
    }
}
