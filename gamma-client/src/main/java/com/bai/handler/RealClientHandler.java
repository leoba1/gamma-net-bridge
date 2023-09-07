package com.bai.handler;

import com.bai.message.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import static com.bai.processor.ClientProcessor.channelPortMap;
import static com.bai.processor.ClientProcessor.portChannelMap;

/**
 * 处理实际本地服务处理器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/17 16:50
 */
@Slf4j
public class RealClientHandler extends ChannelInboundHandlerAdapter {

    private Channel clientChannel;

    private CountDownLatch countDownLatch;

    public RealClientHandler(Channel channel, CountDownLatch countDownLatch) {
        this.clientChannel = channel;
        this.countDownLatch= countDownLatch;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("ttt,{}",ctx.channel().id().asLongText());
        log.info("本地客户端连接成功:"+ctx.channel().remoteAddress());
        Integer port = channelPortMap.get(ctx.channel());
        portChannelMap.put(port,ctx.channel());
        countDownLatch.countDown();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接受到本地服务的响应，准备发送给服务器
        byte[] data = (byte[]) msg;
        Message dataMessage = new Message();
//        String visitorId = SessionFactory.getSession().getId(ctx.channel());
        Integer port = channelPortMap.get(ctx.channel());
        dataMessage.setType(Message.TYPE_TRANSFER);
        dataMessage.setData(data);
        HashMap<String,Object> metaData=new HashMap<>(2,1f);
//        metaData.put("visitorId",visitorId);
        metaData.put("toPort",port);
        dataMessage.setMetaData(metaData);
        clientChannel.writeAndFlush(dataMessage);
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
