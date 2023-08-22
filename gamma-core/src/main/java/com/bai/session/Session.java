package com.bai.session;

import io.netty.channel.Channel;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/23 16:39
 */
public interface Session {
    /**
     * 绑定
     * @param channel 哪个 channel 要绑定会话
     * @param visitorId 绑定的会话
     */
    void bind(Channel channel, String visitorId);


    /**
     * 相互解绑
     * @param channel 哪个 channel 要解绑会话
     */
    void unbind(Channel channel);

    /**
     * 相互解绑
     * @param id 会话 id
     */
    void unbind(String id);

    /**
     * 获取会话
     * @param id 会话 id
     * @return 会话
     */
    Channel getChannel(String id);
    /**
     * 获取会话
     * @param channel 会话
     * @return 会话
     */
    String getId(Channel channel);

    /**
     * 获取容器中的个数
     */
    int size();
}
