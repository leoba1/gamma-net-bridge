package com.bai.processor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/21 14:30
 */
@Slf4j
@Configuration
@ComponentScan
@PropertySource("classpath:config.properties")
public class ConnectTransportProcessor {

    @Autowired
    private Bootstrap transportBootstrap;

    @Value("${server.host}")
    private String remoteHost;

    @Value("${server.port}")
    private int remotePort;

    private static volatile Channel transportChannel;

    public Channel startTransportConnect(){
        try {
            transportChannel = transportBootstrap.connect(remoteHost, remotePort).sync().channel();
            log.info("客户端连接到远程主机:"+ remoteHost + ":" + remotePort);
            log.info("即将连接到本地服务...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return transportChannel;
    }

    //单例模式获取channel
    public static Channel getTransportChannel(){
        if (transportChannel == null || !transportChannel.isActive()){
            synchronized (ConnectTransportProcessor.class){
                if (transportChannel == null || !transportChannel.isActive()) {
                    transportChannel = new ConnectTransportProcessor().startTransportConnect();
                }
            }
        }
        return transportChannel;
    }
}
