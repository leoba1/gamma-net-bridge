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

    public Channel startTransportConnect(){
        Channel transportChannel = null;
        try {
            transportChannel = transportBootstrap.connect(remoteHost, remotePort).sync().channel();
            log.info("客户端连接到远程主机:"+ remoteHost + ":" + remotePort);
            log.info("即将连接到本地服务...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return transportChannel;
    }
}
