package com.bai.handler;

import com.bai.message.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 *反向代理处理器，处理外部发出来的请求
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 17:03
 */
@Slf4j
public class ProxyServerHandler extends ChannelInboundHandlerAdapter {

    private volatile Channel serverChannel;
    private ChannelGroup proxyChannelGroup;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public ProxyServerHandler(NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup,
                              ChannelGroup proxyChannelGroup,Channel serverChannel) {
        this.serverChannel = serverChannel;
        this.proxyChannelGroup = proxyChannelGroup;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        proxyChannelGroup.add(ctx.channel());
        //有请求过来，开启本地客户端连接
        Message message=new Message();
        HashMap<String, Object> map=new HashMap<>();
        map.put("visitorId", ctx.channel().id().asLongText());
        message.setMetaData(map);
        message.setType(Message.TYPE_CONNECT);
        serverChannel.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        proxyChannelGroup.remove(ctx.channel());
        if (proxyChannelGroup.isEmpty()){
            serverChannel.close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        proxyChannelGroup.remove(ctx.channel());
        if (proxyChannelGroup.isEmpty()){
            serverChannel.close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
