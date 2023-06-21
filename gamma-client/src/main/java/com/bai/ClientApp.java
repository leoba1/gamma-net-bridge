package com.bai;

import com.bai.client.TransportClient;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
@Configuration
@ComponentScan("com.bai")
public class ClientApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            //初始化IOC容器
            context=new AnnotationConfigApplicationContext(ClientApp.class);

            TransportClient client = context.getBean(TransportClient.class);
            client.start();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("关闭IOC容器");
        }
    }
}
