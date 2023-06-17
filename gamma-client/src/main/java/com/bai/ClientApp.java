package com.bai;

import com.bai.codec.MessageDecoder;
import com.bai.codec.MessageEncoder;
import com.bai.container.Container;
import com.bai.message.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
@Slf4j
@NoArgsConstructor
public class ClientApp extends Container {

    private volatile Channel channel = null;
    EventLoopGroup group = new NioEventLoopGroup();
    private String host;
    private int port;

    public ClientApp(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            log.info("正在启动服务...");
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new MessageDecoder());
                            ch.pipeline().addLast(new MessageEncoder());
//                            ch.pipeline().addLast(new ClientHandler());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<Message>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    Message message = new Message();
                                    message.setData("hello".getBytes());
                                    ctx.writeAndFlush(message);
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
//                                    Message message=new Message();
//                                    message.setData("qwert".getBytes());
//                                    ctx.writeAndFlush(message);
                                }
                            });

                        }
                    });

            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("客户端连接到: " + host + ":" + port);

            channel.closeFuture().sync().addListener(future -> {
                log.info("关闭中");
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            log.debug("服务错误", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        channel.close();
        group.shutdownGracefully();
        log.info("clientApp关闭服务");
    }

    public static void main(String[] args) {
        ClientApp clientApp = new ClientApp("localhost", 8080);
        clientApp.start();

        RealClientAPP realClientApp =new RealClientAPP("localhost",9090);
//        realClientApp.start();
    }
}
