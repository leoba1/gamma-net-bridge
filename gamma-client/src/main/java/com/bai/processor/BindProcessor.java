package com.bai.processor;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static com.bai.constants.Constants.BIND_CHANNEL;

/**
 * channel绑定处理器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/22 19:40
 */
@Configuration
@ComponentScan
public class BindProcessor {

    @Autowired
    private Channel transportChannel;

    @Autowired
    private Channel realChannel;

    public void bindChannel(){
        //绑定channel关系
        transportChannel.attr(BIND_CHANNEL).set(realChannel);
        realChannel.attr(BIND_CHANNEL).set(transportChannel);
    }

}
