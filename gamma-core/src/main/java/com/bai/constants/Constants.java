package com.bai.constants;

import io.netty.util.AttributeKey;
import io.netty.channel.Channel;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/11 18:39
 */
public interface Constants {
    //魔数
    byte[] MAGIC_NUM={'C','A','F','E'};

    //AttributeMap的Key
    AttributeKey<Channel> BIND_CHANNEL=AttributeKey.newInstance("bind_channel");
}
