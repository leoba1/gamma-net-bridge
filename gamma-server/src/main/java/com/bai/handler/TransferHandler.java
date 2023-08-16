package com.bai.handler;

import com.bai.message.Message;
import com.bai.session.SessionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 15:44
 */
@Slf4j
public class TransferHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        log.info("收到返回的消息");
        Channel proxyChannel = SessionFactory.getSession().getChannel(ctx.channel());
        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(msg.getData());

        proxyChannel.writeAndFlush(buffer);
    }
}
