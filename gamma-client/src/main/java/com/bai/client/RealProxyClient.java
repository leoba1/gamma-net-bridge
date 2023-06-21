package com.bai.client;

import com.bai.container.Container;
import com.bai.handler.TransportClientHandler;
import com.bai.handler.RealClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 本地服务代理客户端
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 15:28
 */
@Slf4j
@NoArgsConstructor
@Configuration
@ComponentScan("com.bai.client")
public class RealProxyClient extends Container {

    @Autowired
    private Bootstrap realProxyBootstrap;

    private String host;
    private int port;
    private volatile Channel channel = null;
    EventLoopGroup group = new NioEventLoopGroup();


    public RealProxyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start(){
        try {
            log.info("正在连接本地服务...");
            realProxyBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch){
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new RealClientHandler());
                        }
                    });
            channel = realProxyBootstrap.connect(host, port).sync().channel();
            log.info("已连接到本地服务: "+host+":"+port);

            channel.closeFuture().sync().addListener(future -> {
                log.info("关闭中");
                group.shutdownGracefully();
            });
        }catch (InterruptedException e){
            log.info("服务错误",e);
        }finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
        group.shutdownGracefully();
        log.info("关闭本地服务连接");
    }

}
