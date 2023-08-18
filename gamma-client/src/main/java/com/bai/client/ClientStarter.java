package com.bai.client;

import com.bai.codec.MessageDecoder;
import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.handler.ClientHandler;
import com.bai.handler.HeartBeatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器启动
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/16 16:36
 */
@Slf4j
public class ClientStarter extends Container {
    NioEventLoopGroup group = new NioEventLoopGroup();

    @Override
    public void start() {
        ChannelInitializer channelInitializer = new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {

                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                ch.pipeline().addLast(new MessageDecoder());
                ch.pipeline().addLast(new MessageEncoder());
                ch.pipeline().addLast(new ClientHandler());
//                ch.pipeline().addLast(new HeartBeatHandler(HeartBeatHandler.READ_IDLE_TIME,HeartBeatHandler.WRITE_IDLE_TIME-5));

            }
        };

        ClientInit clientInit = new ClientInit();
        clientInit.init(group, channelInitializer, "127.0.0.1", 8080);
    }

    @Override
    public void close() {

    }
}
