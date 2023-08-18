package com.bai.handler;

import com.bai.message.Message;
import com.bai.session.SessionFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *反向代理处理器，处理外部发出来的请求，转发给内部服务器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 17:03
 */
@Slf4j
public class ProxyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("代理连接建立成功,channelId:{}", ctx.channel().id());
        //保存channel，及其id
        log.info("当前总服务连接数：{}", SessionFactory.getSession().size());
    }

//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//        //把每个channel进行编号
//        log.debug("收到请求，开始转发，channelId:{}", ctx.channel().id());
//
//
//        //转换成Message消息
//        Message message = new Message();
//        byte[] data = new byte[msg.readableBytes()];
//        msg.readBytes(data);
//        message.setData(data);
//        SessionFactory.getSession().getChannel(ctx.channel()).writeAndFlush(message);
//    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        cause.printStackTrace();
    }
}
