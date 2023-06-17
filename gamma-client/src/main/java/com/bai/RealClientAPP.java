package com.bai;

import com.bai.container.Container;
import com.bai.message.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 15:28
 */
@Slf4j
public class RealClientAPP extends Container {
    private String host;
    private int port;
    private volatile Channel channel = null;
    EventLoopGroup group = new NioEventLoopGroup();

    public RealClientAPP(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start(){
        try {
            log.info("正在连接本地服务...");
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch){
                            ch.pipeline().addLast(new LoggingHandler());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<Message>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

                                }
                            });
                        }
                    });
            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("连接到本地服务: " + host + ":" + port);

            channel.closeFuture().sync().addListener(future -> {
                log.info("关闭中");
                group.shutdownGracefully();
            });
        }catch (InterruptedException e){
            log.debug("服务错误",e);
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
