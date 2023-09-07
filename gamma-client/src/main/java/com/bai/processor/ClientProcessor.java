package com.bai.processor;

import com.bai.client.ClientInit;
import com.bai.handler.RealClientHandler;
import com.bai.message.Message;
import com.bai.session.SessionFactory;
import com.bai.utils.ConfigReaderUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static com.bai.constants.Constants.RANDOM;

/**
 * 处理客户端业务逻辑
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/8/21 21:43
 */
public class ClientProcessor {

    public static final Map<Integer, Channel> portChannelMap = new ConcurrentHashMap<>(6,0.8f,4);;
    public static final Map<Channel, Integer> channelPortMap = new ConcurrentHashMap<>(6,0.8f,4);;

    //本地监听服务器线程池
    private NioEventLoopGroup group = new NioEventLoopGroup();

    /**
     * 处理服务器发过来的处理连接请求
     * @param ctx ctx
     * @param message 消息
     */
    public void doConnect(ChannelHandlerContext ctx, Message message, CountDownLatch countDownLatch) {
        List<String> ports = ConfigReaderUtil.ConfigReaders("client.port");
        Map<String, Object> metaData = message.getMetaData();
//        String visitorId = (String) metaData.get("visitorId");

        for (String port : ports) {
            if (portChannelMap.containsKey(Integer.parseInt(port)+RANDOM)){
                continue;
            }
            ChannelInitializer<NioSocketChannel> channelInitializer= new ChannelInitializer<>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ByteArrayEncoder());
                    ch.pipeline().addLast(new ByteArrayDecoder());
                    ch.pipeline().addLast(new RealClientHandler(ctx.channel(),countDownLatch));
                }
            };
            ClientInit clientInit=new ClientInit();
            String localHost = ConfigReaderUtil.ConfigReader("client.host");
            clientInit.init(group,channelInitializer,localHost,Integer.valueOf(port));

//            channelMap.put(visitorId,clientInit.getChannel());
//            SessionFactory.getSession().bind(clientInit.getChannel(),visitorId);
            portChannelMap.put(Integer.parseInt(port) + RANDOM,clientInit.getChannel());
            channelPortMap.put(clientInit.getChannel(),Integer.valueOf(port));
        }
    }
}
