package com.bai.server;

import com.bai.utils.Regex;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/16 16:33
 */
@Slf4j
public class ServerInit {

    private volatile Channel channel=null;

    public void init(NioEventLoopGroup boosGroup,NioEventLoopGroup workGroup, ChannelInitializer channelInitializer, String host, Integer port){
        Regex.checkHost(host);
        Regex.checkPort(port.toString());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            channel = serverBootstrap.group(boosGroup,workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)//连接队列最大长度,boosGroup线程
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//开启TCP keepalive机制,workerGroup线程
                    //发送时滑动窗口大小，计算最大带宽延迟积(BDP):延迟(50ms)×带宽(4Mbps)/8=31.25KB
                    .childOption(ChannelOption.SO_SNDBUF,7 << 10)
                    .bind(host, port).sync().channel();
            log.info("传输服务已在"+host+":"+port+"启动");

            channel.closeFuture().addListener(future -> {
                channel.close();
            });

        } catch (InterruptedException e) {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Channel getChannel(){
        return channel;
    }
}
