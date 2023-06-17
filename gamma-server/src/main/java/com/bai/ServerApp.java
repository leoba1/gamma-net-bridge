package com.bai;

import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.handler.TransferHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 传输服务器，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
public class ServerApp extends Container {

    //在channelHandler中是不共享的，但是在channel中是共享的
    public static ConcurrentHashMap<String, ChannelHandlerContext> map = new ConcurrentHashMap<>();

    private int port;
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    //volatile关键字的作用是确保多个线程访问该变量时的可见性和有序性,防止数据不一致
    private volatile Channel channel=null;

    public ServerApp(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new MessageEncoder());
//                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new TransferHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)//连接队列最大长度
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//开启TCP keepalive机制
                    //发送时滑动窗口大小，计算最大带宽延迟积(BDP):延迟(50ms)×带宽(4Mbps)/8=31.25KB
                    .childOption(ChannelOption.SO_SNDBUF,7 * 1024);

            ChannelFuture future = bootstrap.bind(port).sync();
            channel = future.channel();
            System.out.println("服务器端口监听:" + port);

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public static void main(String[] args) {
        new ServerApp(8080).start();
    }
}
