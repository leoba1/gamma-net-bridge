package com.bai.server;

import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.handler.TransferHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 传输服务器，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/20 15:47
 */
@Slf4j
@Service
public class TransportServer extends Container {
    //在channelHandler中是不共享的，但是在channel中是共享的
    public static ConcurrentHashMap<String, ChannelHandlerContext> map = new ConcurrentHashMap<>();

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();


    @Override
    @Bean("transportServerBootstrap")
    public ServerBootstrap initServer() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new MessageEncoder());
//                            ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(new TransferHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)//连接队列最大长度,boosGroup线程
                .childOption(ChannelOption.SO_KEEPALIVE, true)//开启TCP keepalive机制,workerGroup线程
                //发送时滑动窗口大小，计算最大带宽延迟积(BDP):延迟(50ms)×带宽(4Mbps)/8=31.25KB
                .childOption(ChannelOption.SO_SNDBUF,7 << 10);
        return bootstrap;
    }


    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
