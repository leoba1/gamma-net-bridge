package com.bai.handler;

import com.bai.message.Message;
import com.bai.server.TransportServer;
import com.bai.session.SessionFactory;
import com.bai.temp;
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
        temp.map.put("justTest", ctx.channel());

//        if (TransportServer.map.size() == 0) {
//            TransportServer.map.put("test1", ctx);
//        } else {
//            TransportServer.map.put("test2", ctx);
//        }
//        System.out.println(ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        log.info("收到返回的消息");
        Channel proxyChannel = SessionFactory.getSession().getChannel(ctx.channel());
        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(msg.getData());
//        buffer.writeBytes("<h1>hello world</h1>".getBytes());

        proxyChannel.writeAndFlush(buffer);

//        ChannelHandlerContext client = TransportServer.map.get("test1");
//        if (client == null) {
//            System.out.println("代理连接未建立！！！");
//            return;
//        }
//        client.writeAndFlush(msg);
//        ctx.writeAndFlush(message);
    }
}
