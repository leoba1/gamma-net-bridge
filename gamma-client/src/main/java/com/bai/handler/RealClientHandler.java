package com.bai.handler;

import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 被代理服务的信息处理
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 16:50
 */
@Slf4j
public class RealClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接到本地的服务");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    }
}
