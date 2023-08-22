package com.bai.handler;

import com.bai.message.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 处理实际本地服务处理器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 16:50
 */
@Slf4j
public class RealClientHandler extends ChannelInboundHandlerAdapter {

    private Channel clientChannel;

    public RealClientHandler(Channel channel) {
        this.clientChannel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("本地客户端连接成功:"+ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接受到本地服务的响应，准备发送给服务器
        byte[] data = (byte[]) msg;
        Message dataMessage = new Message();
        dataMessage.setType(Message.TYPE_TRANSFER);
        dataMessage.setData(data);
        HashMap<String,Object> metaData=new HashMap<>();
        metaData.put("visitorId",clientChannel.id().asLongText());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
