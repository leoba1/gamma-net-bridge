package com.bai.session;

import io.netty.channel.Channel;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/23 16:39
 */
public interface Session {
    /**
     * 绑定两个channel，相互绑定
     * @param myChannel 哪个 channel 要绑定
     * @param otherChannel 绑定
     */
    void bind(Channel myChannel, Channel otherChannel);

    /**
     * 相互解绑
     * @param channel 哪个 channel 要解绑会话
     */
    void unbind(Channel channel);

    /**
     * 获取属性
     * @param channel 哪个 channel
     * @param name 属性名
     * @return 属性值
     */
    Object getAttribute(Channel channel, String name);

    /**
     * 设置属性
     * @param channel 哪个 channel
     * @param name 属性名
     * @param value 属性值
     */
    void setAttribute(Channel channel, String name, Channel value);

    /**
     * 根据用户名获取 channel
     * @param channel 用户名
     * @return channel
     */
    Channel getChannel(Channel channel);

    /**
     * 获取容器中的个数
     */
    int size();
}
