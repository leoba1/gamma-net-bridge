package com.bai.processor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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

    @Bean("realChannel")
    public Channel startRealConnect(){
        Channel realChannel = null;
        try {
            realChannel = realProxyBootstrap.connect(localHost, localPort).sync().channel();
            log.info("已连接到本地服务:"+ localHost + ":" + localPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return realChannel;
    }


}
