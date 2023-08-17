package com.bai.container;

/**
 * netty服务器模板
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/8 20:07
 */
public abstract class Container {

        /**
        * 启动服务器
        */
        public abstract void start();

        /**
        * 关闭服务器
        */
        public abstract void close();
}
