package com.bai;

import com.bai.container.Container;
import com.bai.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
@Slf4j
public class ClientApp extends Container {

    private Channel channel=null;
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
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ClientHandler());

                            URI uri = new URI("http://localhost:9090/news/page");
                            // 构造GET请求
                            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
                            // 添加Host头部信息
                            request.headers().set(HttpHeaderNames.HOST, "localhost:9090");
                            ch.writeAndFlush(request);
                        }
                    });

            channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("客户端连接到: " + host + ":" + port);

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.debug("服务错误",e);
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
        new ClientApp("localhost", 9090).start();
    }
}
