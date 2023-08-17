package com.bai.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/16 16:36
 */
@Slf4j
public class ClientInit {

    private volatile Channel channel=null;

    /**
     * 配置客户端
     * @param group 线程组留空是为了后续多端口服务的情况
     * @param channelInitializer 通道初始化器
     * @param host ip地址
     * @param port 端口号
     */
    public void start(NioEventLoopGroup group, ChannelInitializer channelInitializer, String host, Integer port){
        log.info("正在启动服务...");
        try {
            Bootstrap transportBootstrap = new Bootstrap();
            channel = transportBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    //保持长连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(channelInitializer)
                    .connect(host, port).sync().channel();
            channel.closeFuture().addListener(future -> {
                channel.close();
            });

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
