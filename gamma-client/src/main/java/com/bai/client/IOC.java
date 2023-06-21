package com.bai.client;

import io.netty.bootstrap.Bootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/21 15:12
 */
@Component
public class IOC{

    @Bean("transportBootstrap")
    public Bootstrap transportBootstrap(){
        return new Bootstrap();
    }

    @Bean("realProxyBootstrap")
    public Bootstrap realProxyBootstrap(){
        return new Bootstrap();
    }

}
