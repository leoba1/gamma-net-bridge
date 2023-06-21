package com.bai;

import com.bai.processor.ConnectTransportProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 传输服务器，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
@Configuration
@ComponentScan
public class ServerApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            //初始化IOC容器
            context=new AnnotationConfigApplicationContext(ServerApp.class);

            ConnectTransportProcessor bean = context.getBean(ConnectTransportProcessor.class);
            bean.startServerConnect();

            // 注册关闭钩子
            context.registerShutdownHook();
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}
