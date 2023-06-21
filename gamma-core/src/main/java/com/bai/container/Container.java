package com.bai.container;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;

/**
 * netty服务器模板
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/8 20:07
 */
public abstract class Container {
    //初始化客户端
    public Bootstrap initClient() {
        return new Bootstrap();
    }

    //初始化服务器
    public ServerBootstrap initServer() {
        return new ServerBootstrap();
    }

    //关闭服务器
    public abstract void stop();
}
