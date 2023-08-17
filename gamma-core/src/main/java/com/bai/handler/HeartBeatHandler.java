package com.bai.handler;

import com.bai.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 心跳处理器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/17 11:40
 */
@Slf4j
public class HeartBeatHandler extends IdleStateHandler {

    public static final int READ_IDLE_TIME=30;
    public static final int WRITE_IDLE_TIME=20;


    public HeartBeatHandler(long readerIdleTime, long writerIdleTime) {
        super(readerIdleTime, writerIdleTime, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt.state() == IdleState.WRITER_IDLE) {
            // 写超时
            // 发送心跳消息给服务器
            Message message=new Message();
            message.setType(Message.TYPE_HEARTBEAT);
            ctx.writeAndFlush(message);
        } else if (evt.state() == IdleState.READER_IDLE) {
            // 读超时
            log.info("连接出错，准备关闭");
            ctx.channel().close();
        }
        super.channelIdle(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        log.info("发生异常");
        cause.printStackTrace();
    }
}
