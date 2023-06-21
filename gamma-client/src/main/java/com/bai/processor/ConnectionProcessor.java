package com.bai.processor;

import io.netty.bootstrap.Bootstrap;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/21 14:30
 */
public class ConnectionProcessor {
    public ConnectionProcessor(Bootstrap transportBootstrap) {
        this.transportBootstrap = transportBootstrap;
    }


    private Bootstrap transportBootstrap;
    private Bootstrap realBootstrap;
}
