package com.bai.processor;

import io.netty.channel.Channel;
import org.springframework.context.annotation.Configuration;

import static com.bai.constants.Constants.BIND_CHANNEL;

/**
 * channel绑定处理器
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/22 19:40
 */
@Configuration
public class BindProcessor {

    public void bindChannel(){
        Channel transportChannel = ConnectTransportProcessor.getTransportChannel();
        Channel realChannel = ConnectRealProcessor.getRealChannel();
        //绑定channel关系
        transportChannel.attr(BIND_CHANNEL).set(realChannel);
        realChannel.attr(BIND_CHANNEL).set(transportChannel);
    }

}
