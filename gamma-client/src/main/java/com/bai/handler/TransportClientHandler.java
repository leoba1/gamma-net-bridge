package com.bai.handler;

import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

import static com.bai.constants.Constants.BIND_CHANNEL;


/**
 * 接受来自服务器的信息处理
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/8 20:07
 */
@Slf4j
public class TransportClientHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message message = new Message();
        message.setData("hello".getBytes());
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Channel realChannel = ctx.channel().attr(BIND_CHANNEL).get();
        if (realChannel == null){
            log.info("连接已断开XD");
        }

        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(msg.getData());

        String inputStr = buffer.toString(Charset.defaultCharset());
        String outputStr = inputStr.replaceAll("localhost:9090", "localhost:8080");
        ByteBuf output = Unpooled.copiedBuffer(outputStr, Charset.defaultCharset());

        realChannel.writeAndFlush(output);

//        realChannel.writeAndFlush(buffer);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel realChannel = ctx.channel().attr(BIND_CHANNEL).get();
        if (realChannel == null && !realChannel.isActive()){
            realChannel.close();
        }
        ctx.channel().close();
        cause.printStackTrace();
    }
}
