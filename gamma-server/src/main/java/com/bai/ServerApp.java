package com.bai;

import com.bai.codec.MessageDecoder;
import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.handler.ServerHandler;
import com.bai.message.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
public class ServerApp extends Container {

    private ConcurrentHashMap<ChannelId,Channel> map=new ConcurrentHashMap<>();
    private int port;

    public ServerApp(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new MessageEncoder());
//                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    map.put(ctx.channel().id(),ctx.channel());
                                    System.out.println(ctx.channel().id());
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    Message message=new Message();
                                    message.setData("world".getBytes());
                                    Channel client = map.get(ctx.channel().id());
                                    client.writeAndFlush(message);
                                    ctx.writeAndFlush(message);
                                }

//                                @Override
//                                protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
//                                    Message message=new Message();
//                                    message.setData("world".getBytes());
//                                    ctx.writeAndFlush(message);
//                                }
                            });
//                            ch.pipeline().addLast(new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)//连接队列最大长度
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//开启TCP keepalive机制
                    //发送时滑动窗口大小，计算最大带宽延迟积(BDP):延迟(50ms)×带宽(4Mbps)/8=31.25KB
                    .childOption(ChannelOption.SO_SNDBUF,31 * 1024);

            ChannelFuture future = bootstrap.bind(port).sync();
            Channel channel = future.channel();
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
//        channel.close();
//        boss.shutdownGracefully();
//        worker.shutdownGracefully();
    }

    public static void main(String[] args) {
        new ServerApp(8080).start();
    }
}
