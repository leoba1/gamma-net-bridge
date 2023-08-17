package com.bai.client;

import com.bai.codec.MessageDecoder;
import com.bai.codec.MessageEncoder;
import com.bai.handler.TransportClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/16 16:36
 */
@Slf4j
public class ClientStarter {
    NioEventLoopGroup group = new NioEventLoopGroup();
    public void start() {
        ChannelInitializer channelInitializer=new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

//                        ch.pipeline().addLast(new MessageDecoder());
//                        ch.pipeline().addLast(new MessageEncoder());
//                        ch.pipeline().addLast(new TransportClientHandler());

            }
        };

        ClientInit clientInit=new ClientInit();
        clientInit.start(group,channelInitializer,"localhost",8080);
    }
}
