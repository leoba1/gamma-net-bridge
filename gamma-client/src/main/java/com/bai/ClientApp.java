package com.bai;

import com.bai.processor.BindProcessor;
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
@ComponentScan
public class ClientApp {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = null;
        try {
            //初始化IOC容器
            context=new AnnotationConfigApplicationContext(ClientApp.class);
            //获取bean
            BindProcessor bean = context.getBean(BindProcessor.class);
            bean.bindChannel();

            // 注册关闭钩子,在程序退出时关闭
            context.registerShutdownHook();
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}
