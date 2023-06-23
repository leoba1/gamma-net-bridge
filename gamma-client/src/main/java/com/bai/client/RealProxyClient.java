package com.bai.client;

import com.bai.codec.MessageDecoder;
import com.bai.container.Container;
import com.bai.handler.RealClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * 本地服务代理客户端
 *
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 15:28
 */
@Slf4j
@NoArgsConstructor
@Service
public class RealProxyClient extends Container {
    NioEventLoopGroup group = new NioEventLoopGroup();

    @Bean("realProxyBootstrap")
    @Override
    public Bootstrap initClient() {
        log.info("正在连接本地服务...");
        Bootstrap realProxyBootstrap = new Bootstrap();
        realProxyBootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(new RealClientHandler());
                    }
                });
        return realProxyBootstrap;
    }

    @Override
    public void stop() {
        group.shutdownGracefully();
        log.info("关闭本地服务连接");
    }

}
