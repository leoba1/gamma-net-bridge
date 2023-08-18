package com.bai.processor;

import com.bai.message.Message;
import com.bai.server.ServerInit;
import com.bai.utils.ConfigReaderUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;
import java.util.Map;

/**
 * 处理注册、消息转发逻辑
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/18 21:00
 */
public class LogicProcessor {

    //共享的线程池
    private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * 处理注册逻辑，开启对应的端口监听
     */
    public void ProcessReg(ChannelHandlerContext ctx, Message message) {
        Map<String, Object> metaData = message.getMetaData();
        String token= (String)metaData.get("token");
        //TODO 校验token

        //启动对应的端口监听
        List<String> ports =(List<String>) metaData.get("ports");
        String host = ConfigReaderUtil.ConfigReader("server.host");

        ports.forEach(port->{
            ChannelInitializer<NioSocketChannel> channelInitializer = new ChannelInitializer<>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {

                }
            };
            ServerInit serverInit=new ServerInit();
            serverInit.init(bossGroup, workerGroup, channelInitializer, host, Integer.parseInt(port));
        });
    }
}
