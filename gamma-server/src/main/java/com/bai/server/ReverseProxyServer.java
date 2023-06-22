package com.bai.server;


import com.bai.container.Container;
import com.bai.handler.ProxyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * 反向代理服务器,把代理的信息传给ServerApp
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 17:03
 */
@Slf4j
@Service
public class ReverseProxyServer extends Container {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
    @Bean("proxyServerBootstrap")
    public ServerBootstrap initServer() {
        ServerBootstrap proxyBootstrap = new ServerBootstrap();
        proxyBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        ch.pipeline().addLast(new ProxyServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)//连接队列最大长度,boosGroup线程
                .childOption(ChannelOption.SO_KEEPALIVE, true)//开启TCP keepalive机制,workerGroup线程
                //发送时滑动窗口大小，计算最大带宽延迟积(BDP):延迟(50ms)×带宽(4Mbps)/8=31.25KB
                .childOption(ChannelOption.SO_SNDBUF,7 << 10);
        return proxyBootstrap;
    }


    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
