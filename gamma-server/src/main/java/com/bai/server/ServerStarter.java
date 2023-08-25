package com.bai.server;

import com.bai.codec.MessageDecoder;
import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.handler.ServerHandler;
import com.bai.utils.ConfigReaderUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/16 16:33
 */
public class ServerStarter extends Container {

    private final NioEventLoopGroup boosGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void start() {
        try {
            ChannelInitializer channelInitializer = new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {

                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new MessageDecoder());
                    ch.pipeline().addLast(new MessageEncoder());
                    ch.pipeline().addLast(new ServerHandler(channelGroup));
    //                ch.pipeline().addLast(new HeartBeatHandler(HeartBeatHandler.READ_IDLE_TIME, HeartBeatHandler.WRITE_IDLE_TIME));

                }
            };

            ServerInit serverInit = new ServerInit();
            String host = ConfigReaderUtil.ConfigReader("server.host");
            int port = Integer.parseInt(ConfigReaderUtil.ConfigReader("server.port"));
            serverInit.init(boosGroup, workerGroup, channelInitializer, host, port);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        boosGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
