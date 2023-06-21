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
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.beans.JavaBean;

/**
 * 传输客户端，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/20 16:34
 */
@Slf4j
@NoArgsConstructor
@Configuration
@ComponentScan("com.bai.client")
public class TransportClient extends Container {

    @Autowired
    private Bootstrap transportBootstrap;

    private volatile Channel channel = null;
    EventLoopGroup group = new NioEventLoopGroup();
    private String host="localhost";
    private int port=8080;

    public TransportClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            log.info("正在启动服务...");
            transportBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new MessageEncoder());
                            ch.pipeline().addLast(new TransportClientHandler());
                        }
                    });

            channel = transportBootstrap.connect(host, port).sync().channel();
            log.info("客户端连接到远程主机:"+ host + ":" + port);
//            log.info("即将连接到本地服务...");
//
//            channel.closeFuture().sync().addListener(future -> {
//                log.info("关闭中");
//                group.shutdownGracefully();
//            });

        } catch (InterruptedException e) {
            log.debug("服务错误", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
        group.shutdownGracefully();
        log.info("clientApp关闭服务");
    }
}
