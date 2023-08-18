package com.bai.handler;

import com.bai.message.Message;
import com.bai.processor.LogicProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/8 20:07
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static LogicProcessor logicProcessor = new LogicProcessor();

    //是否注册
    private boolean isReg = false;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的连接:" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        byte type = message.getType();
        if (isReg){//已经注册了
            //处理消息转发逻辑

        }else if (type == Message.REG){
            //处理注册逻辑
            logicProcessor.ProcessReg(ctx,message);
            //注册成功
            isReg = true;
        }else {
            //未注册，且不是注册消息，直接关闭连接
            ctx.channel().close();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
