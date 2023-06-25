package com.bai.processor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

/**
 * 连接本地服务
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/21 16:43
 */
@Slf4j
@Configuration
@ComponentScan
@PropertySource("classpath:config.properties")
public class ConnectRealProcessor {

    @Autowired
    private Bootstrap realProxyBootstrap;

    @Value("${client.host}")
    private String localHost;

    @Value("${client.port}")
    private int localPort;

    private static volatile Channel realChannel;

    public Channel startRealConnect(){
        try {
            realChannel = realProxyBootstrap.connect(localHost, localPort).sync().channel();
            log.info("已连接到本地服务:"+ localHost + ":" + localPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return realChannel;
    }

    //单例模式获取channel
    public static Channel getRealChannel(){
        if (realChannel == null || !realChannel.isActive()){
            synchronized (ConnectRealProcessor.class){
                if (realChannel == null || !realChannel.isActive()) {
                    realChannel = new ConnectRealProcessor().startRealConnect();
                }
            }
        }
        return realChannel;
    }

}
