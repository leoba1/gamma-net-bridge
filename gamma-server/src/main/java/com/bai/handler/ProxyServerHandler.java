package com.bai.handler;

import com.bai.message.Message;
import com.bai.session.SessionFactory;
import com.bai.temp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 17:03
 */
public class ProxyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = temp.map.get("justTest");
        SessionFactory.getSession().bind(ctx.channel(), channel);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //转换成Message消息
        Message message = new Message();
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        message.setData(data);
        SessionFactory.getSession().getChannel(ctx.channel()).writeAndFlush(message);
    }
}
