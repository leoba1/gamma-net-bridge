package com.bai.handler;

import com.bai.message.Message;
import com.bai.session.SessionFactory;
import com.bai.temp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 *反向代理处理器，处理外部发出来的请求，转发给内部服务器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 17:03
 */
@Slf4j
public class ProxyServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("代理连接建立成功,channelId:{}", ctx.channel().id());
        //保存channel，及其id
        Channel channel = temp.map.get("justTest");
        SessionFactory.getSession().bind(ctx.channel(), channel);
        log.info("当前总服务连接数：{}", SessionFactory.getSession().size());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //把每个channel进行编号
        log.debug("收到请求，开始转发，channelId:{}", ctx.channel().id());


        //转换成Message消息
        Message message = new Message();
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        message.setData(data);
        SessionFactory.getSession().getChannel(ctx.channel()).writeAndFlush(message);
    }
}
