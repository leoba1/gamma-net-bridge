package com.bai.processor;

import com.bai.handler.ProxyServerHandler;
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

    private ConcurrentHashMap<String,Channel> portMap=new ConcurrentHashMap<>();

    /**
     * 处理注册逻辑，开启对应的端口监听
     * @param ctx
     * @param message
     */
    public void ProcessReg(ChannelHandlerContext ctx, Message message) {
        Map<String, Object> metaData = message.getMetaData();
        String token= (String)metaData.get("token");
        //TODO 校验token

        //启动对应的端口监听
        List<String> ports =(List<String>) metaData.get("ports");
        String host = ConfigReaderUtil.ConfigReader("server.host");

        for (String port : ports) {
            if (portMap.containsKey(port)){
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
            serverInit.init(bossGroup, workerGroup, channelInitializer, host, Integer.parseInt(port));
            portMap.put(port,serverInit.getChannel());
        }
    }

}
