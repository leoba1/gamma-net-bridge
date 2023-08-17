package com.bai.client;

import com.bai.codec.MessageDecoder;
import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.handler.TransportClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 传输客户端，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/20 16:34
 */
@Slf4j
@Deprecated
public class TransportClient extends Container {
    //线程组
    NioEventLoopGroup group = new NioEventLoopGroup();

    @Override
    public Bootstrap initClient() {
        log.info("正在启动服务...");
        Bootstrap transportBootstrap = new Bootstrap();
        transportBootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(new MessageEncoder());
//                        ch.pipeline().addLast(new TestHandler());
                        ch.pipeline().addLast(new TransportClientHandler());
                    }
                });
        return transportBootstrap;

    }

    @Override
    public void stop() {
        group.shutdownGracefully();
        log.info("clientApp关闭服务");
    }
}
