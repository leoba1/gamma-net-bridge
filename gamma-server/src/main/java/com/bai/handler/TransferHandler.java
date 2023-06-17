package com.bai.handler;

import com.bai.ServerApp;
import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 15:44
 */
@Slf4j
public class TransferHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ServerApp.map.size() == 0) {
            ServerApp.map.put("test1", ctx);
        } else {
            ServerApp.map.put("test2", ctx);
        }
        System.out.println(ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        Message message = new Message();
        byte[] data = new byte[msg.readableBytes()];
        msg.readBytes(data);
        message.setData(data);
        ChannelHandlerContext client = ServerApp.map.get("test1");
        if (client == null) {
            System.out.println("代理连接未建立！！！");
            return;
        }
        client.writeAndFlush(message);
//        ctx.writeAndFlush(message);
    }
}
