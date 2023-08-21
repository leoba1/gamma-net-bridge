package com.bai.handler;

import com.bai.message.Message;
import com.bai.processor.ClientProcessor;
import com.bai.utils.ConfigReaderUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bai.constants.Constants.ERROR_MSG;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/17 11:21
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static ClientProcessor clientProcessor = new ClientProcessor();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message message = new Message();
        //发送注册消息
        message.setType(Message.REG);
        //将本地的端口映射信息发送给服务器
        List<String> ports = ConfigReaderUtil.ConfigReaders("visitor.port");
        HashMap<String, Object> portsMapping = new HashMap<>();
        portsMapping.put("ports", ports);
        //发送token验证信息
        String token = ConfigReaderUtil.ConfigReader("token");
        portsMapping.put("token", token);
        //发送给服务器
        message.setMetaData(portsMapping);
        log.info("正在连接服务器");
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        byte type = message.getType();
        switch (type) {
            case Message.TYPE_CONNECT:
                //开启本地端口监听
                clientProcessor.doConnect(ctx,message);

                break;
            case Message.TYPE_TRANSFER:
                //处理数据传输逻辑
                //TODO

                break;
            case Message.TYPE_DISCONNECT:
                //断开连接
                ctx.channel().close();
                break;
            case Message.TYPE_ERROR:
                //异常信息
                Map<String, Object> metaData = message.getMetaData();
                Throwable err = (Throwable) metaData.get(ERROR_MSG);
                log.info("服务端发送错误信息!");
                err.printStackTrace();
                break;
            case Message.TYPE_HEARTBEAT:
                //心跳,不处理
                break;
            case Message.CONFIRM:
                //注册确认
                log.info("注册成功!");
                break;
            default:
                //非法请求
                log.info("非法请求");
                ctx.channel().close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
