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

/**
 * 传输客户端，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/20 16:34
 */
@Slf4j
@NoArgsConstructor
public class TransportClient extends Container {

    private volatile Channel channel = null;
    EventLoopGroup group = new NioEventLoopGroup();
    private String host;
    private int port;

    public TransportClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            log.info("正在启动服务...");
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
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

            channel = bootstrap.connect(host, port).sync().channel();
            log.info("客户端连接到远程主机:"+ host + ":" + port);

            channel.closeFuture().sync().addListener(future -> {
                log.info("关闭中");
                group.shutdownGracefully();
            });

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
