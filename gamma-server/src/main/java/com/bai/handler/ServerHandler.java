package com.bai.handler;

import com.bai.message.Message;
import com.bai.processor.ServerProcessor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static com.bai.constants.Constants.ERROR_MSG;

/**
 * 处理客户端的消息
 *
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/8 20:07
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    //visitorId和channel的映射
    public static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>(8, 0.8f, 4);
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
        log.debug("ttt,{}",ctx.channel().id().asLongText());
        log.info("有新的连接:" + ctx.channel().remoteAddress());
        serverChannelGroup.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        byte type = message.getType();
        if (isReg) {//已经注册了
            switch (type) {
                case Message.TYPE_TRANSFER:
                    //处理数据传输逻辑
                    serverProcessor.ProcessTransfer(ctx, message);
                    break;
                case Message.TYPE_DISCONNECT:
                    //处理断开连接逻辑
                    ctx.channel().close();
                    break;
                case Message.TYPE_HEARTBEAT:
                    //不处理心跳
                    break;
                case Message.TYPE_ERROR:
                    //处理错误逻辑
                    Throwable cause = (Throwable) message.getMetaData().get(ERROR_MSG);
                    cause.printStackTrace();
                    break;
//                case Message.TYPE_CONNECT:
//
//                    break;
                default:
                    //未知消息类型
                    log.info("未知消息类型!");
                    ctx.channel().close();
                    break;
            }
        } else if (type == Message.REG) {
            //处理注册逻辑
            serverProcessor.ProcessReg(ctx, message);
            //注册成功
            Message confirmMessage = new Message();
            confirmMessage.setType(Message.CONFIRM);
            isReg = true;
        } else {
            //未注册，且不是注册消息，直接关闭连接
            log.info("非法请求!");
            ctx.channel().close();
        }

    }

//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        // 移除信息
//        isReg = false;
//        serverChannelGroup.remove(ctx.channel());
//        ctx.close();
//    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        Message message = new Message();
//        message.setType(Message.TYPE_ERROR);
//        HashMap<String, Object> map = new HashMap<>(5, 0.8f);
//        map.put(ERROR_MSG, cause);
//        message.setMetaData(map);
//        ctx.channel().writeAndFlush(message);
//        serverChannelGroup.remove(ctx.channel());
//        cause.printStackTrace();
//        ctx.close();
//    }

}
