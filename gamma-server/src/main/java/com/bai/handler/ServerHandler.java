package com.bai.handler;

import com.bai.message.Message;
import com.bai.processor.ServerProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理客户端的消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/8 20:07
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static ServerProcessor serverProcessor = new ServerProcessor();
    //是否注册
    private boolean isReg = false;

    //所有的客户端连接
    private ChannelGroup serverChannelGroup;

    public ServerHandler(ChannelGroup serverChannelGroup) {
        this.serverChannelGroup = serverChannelGroup;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有新的连接:" + ctx.channel().remoteAddress());
        serverChannelGroup.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        byte type = message.getType();
        if (isReg){//已经注册了
            //处理消息转发逻辑
            //TODO
        }else if (type == Message.REG){
            //处理注册逻辑
//            ServerProcessor.getInstance().ProcessReg(ctx,message);
            serverProcessor.ProcessReg(ctx,message);
            //注册成功
            Message confirmMessage = new Message();
            confirmMessage.setType(Message.CONFIRM);
            isReg = true;
        }else {
            //未注册，且不是注册消息，直接关闭连接
            log.info("非法请求!");
            ctx.channel().close();
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //TODO 移除信息
        serverChannelGroup.remove(ctx.channel());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        serverChannelGroup.remove(ctx.channel());

        cause.printStackTrace();
        ctx.close();
    }

}
