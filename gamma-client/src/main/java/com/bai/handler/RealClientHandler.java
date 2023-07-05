package com.bai.handler;

import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import static com.bai.constants.Constants.BIND_CHANNEL;

/**
 * 被代理服务的信息处理
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 16:50
 */
@Slf4j
@Configuration
public class RealClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("已连接到本地的服务");
//        ScheduledExecutor.Executor(() -> {
//            System.out.println("定时任务已执行");
//            if (!ctx.channel().isActive() && ctx.channel() == null){
//                bindProcessor.bindChannel();
//            }
//        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("收到本地服务的消息");
        Channel transportChannel = ctx.channel().attr(BIND_CHANNEL).get();
        Message message=new Message();
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        message.setData(bytes);
        transportChannel.writeAndFlush(message);
    }
}
