package com.bai.handler;

import com.bai.constants.Constants;
import com.bai.message.Message;
import com.bai.server.TransportServer;
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


        if (TransportServer.map.size() == 0) {
            TransportServer.map.put("test1", ctx);
        } else {
            TransportServer.map.put("test2", ctx);
        }
        System.out.println(ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        ChannelHandlerContext client = TransportServer.map.get("test1");
        if (client == null) {
            System.out.println("代理连接未建立！！！");
            return;
        }
        client.writeAndFlush(msg);
//        ctx.writeAndFlush(message);
    }
}
