package com.bai.processor;

import com.bai.handler.ProxyServerHandler;
import com.bai.handler.ServerHandler;
import com.bai.message.Message;
import com.bai.server.ServerInit;
import com.bai.utils.ConfigReaderUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理注册、消息转发逻辑
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/18 21:00
 */
public class ServerProcessor {

    //同一个serverHandler共享的线程池
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ChannelGroup proxyChannelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final Map<Integer,Channel> portChannelMap=new ConcurrentHashMap<>(6,0.8f,4);;
    public final static Map<Integer,Integer> portMap=new ConcurrentHashMap<>(6,0.8f,4);;

    /**
     * 处理注册逻辑，开启对应的端口监听
     * @param ctx
     * @param message
     */
    public void ProcessReg(ChannelHandlerContext ctx, Message message) {
        Map<String, Object> metaData = message.getMetaData();
        String token= (String)metaData.get("token");
        //TODO 校验token

        List<Integer> clients =(List<Integer>) metaData.get("clients");
        //启动对应的端口监听
        List<Integer> visitors =(List<Integer>) metaData.get("visitors");

        for (int i = 0; i < clients.size(); i++) {
            portMap.put(visitors.get(i),clients.get(i));
        }

        String host = ConfigReaderUtil.ConfigReader("server.host");

        for (int visitor : visitors) {
            if (portChannelMap.containsKey(visitor)){
                continue;
            }

            ChannelInitializer<NioSocketChannel> channelInitializer = new ChannelInitializer<>() {
                @Override
                protected void initChannel(NioSocketChannel ch) {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ByteArrayEncoder());
                    ch.pipeline().addLast(new ByteArrayDecoder());
                    ch.pipeline().addLast(new ProxyServerHandler(bossGroup, workerGroup, proxyChannelGroup, ctx.channel()));
                }
            };
            ServerInit serverInit = new ServerInit();
            serverInit.init(bossGroup, workerGroup, channelInitializer, host, visitor);
            portChannelMap.put(visitor,serverInit.getChannel());
        }
    }

    /**
     * 处理数据传输逻辑
     * @param ctx ctx
     * @param message 消息
     */
    public void ProcessTransfer(ChannelHandlerContext ctx, Message message) {
        Map<String, Object> metaData = message.getMetaData();
        String visitorId = (String) metaData.get("visitorId");
        Channel proxyChannel = ServerHandler.channelMap.get(visitorId);
        if (proxyChannel == null) {
            throw new IllegalArgumentException("channel不存在!");
        }
        byte[] data = message.getData();
        proxyChannel.writeAndFlush(data);
    }
}
