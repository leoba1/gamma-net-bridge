package com.bai.processor;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/21 17:33
 */
@Slf4j
@Configuration
@ComponentScan("com.bai.server")
@PropertySource("classpath:config.properties")
public class ConnectTransportProcessor {

    @Autowired
    private ServerBootstrap transportServerBootstrap;

    @Value("${server.port}")
    private int port;

    public Channel startServerConnect() {
        Channel channel = null;
        try {
            channel = transportServerBootstrap.bind(port).sync().channel();
            log.info("传输服务已在port:"+port+"上开启");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }

}
