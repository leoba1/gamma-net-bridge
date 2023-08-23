package com.bai.handler;

import com.bai.message.Message;
import com.bai.processor.ClientProcessor;
import com.bai.session.SessionFactory;
import com.bai.utils.ConfigReaderUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.bai.constants.Constants.*;

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
        List<String> visitorsStr = ConfigReaderUtil.ConfigReaders("visitor.port");
        List<String> clientsStr = ConfigReaderUtil.ConfigReaders("client.port");

        List<Integer> visitors=new ArrayList<>();
        List<Integer> clients=new ArrayList<>();
        for (int i = 0; i < visitorsStr.size(); i++) {
            visitors.add(Integer.valueOf(visitorsStr.get(i)));
        }
        for (int i = 0; i < clientsStr.size(); i++) {
            clients.add(Integer.valueOf(clientsStr.get(i)));
        }
        if (visitors.size() != clients.size()) {
            log.info("配置文件错误!");
            ctx.channel().close();
            return;
        }
        HashMap<String, Object> portsMapping = new HashMap<>(5,0.8f);
        portsMapping.put("visitors", visitors);
        portsMapping.put("clients", clients);
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
            case Message.CONFIRM:
                //开启本地端口监听
                clientProcessor.doConnect(ctx,message);
                break;
            case Message.TYPE_TRANSFER:
                //处理数据传输逻辑
                Map<String, Object> metaData = message.getMetaData();
                String visitorId = (String) metaData.get("visitorId");
                int fromPort =(int) metaData.get(FROM);
                int toPort = (int) metaData.get(TO);

                Channel localChannel = ClientProcessor.portChannelMap.get(String.valueOf(toPort));

//                Channel localChannel = SessionFactory.getSession().getChannel(visitorId);
                if (localChannel == null) {
                    log.info("本地连接不存在!");
                    break;
                }
                byte[] data = message.getData();
                localChannel.writeAndFlush(data);
                break;
            case Message.TYPE_DISCONNECT:
                //断开连接
                ctx.channel().close();
                break;
            case Message.TYPE_ERROR:
                //异常信息
                Map<String, Object> errMetaData = message.getMetaData();
                Throwable err = (Throwable) errMetaData.get(ERROR_MSG);
                log.info("服务端发送错误信息!");
                err.printStackTrace();
                break;
            case Message.TYPE_HEARTBEAT:
                //心跳,不处理
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
