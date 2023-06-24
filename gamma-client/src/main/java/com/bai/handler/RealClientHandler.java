package com.bai.handler;

import com.bai.executor.ScheduledExecutor;
import com.bai.processor.BindProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 被代理服务的信息处理
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 16:50
 */
@Slf4j
@Configuration
public class RealClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    BindProcessor bindProcessor;
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
    }
}
