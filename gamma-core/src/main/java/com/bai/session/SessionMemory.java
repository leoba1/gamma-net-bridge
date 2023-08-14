package com.bai.session;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/23 16:39
 */
public class SessionMemory implements Session{
    private final ConcurrentHashMap<Channel,ConcurrentHashMap<String,Channel>> channelAttributesMap =
            new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Channel,Channel> channelBind = new ConcurrentHashMap<>();

    @Override
    public void bind(Channel myChannel, Channel otherChannel) {
//        myChannel.remoteAddress();
//        myChannel.localAddress();
        channelBind.put(myChannel,otherChannel);
        channelBind.put(otherChannel,myChannel);
    }

    @Override
    public void unbind(Channel channel) {
        Channel removedChannel = channelBind.remove(channel);
        channelBind.remove(removedChannel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributesMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Channel value) {
        ConcurrentHashMap<String, Channel> map = new ConcurrentHashMap<>();
        map.put(name,value);
        channelAttributesMap.put(channel,map);
    }

    @Override
    public Channel getChannel(Channel channel) {
        return channelBind.get(channel);
    }

    @Override
    public int size() {
        return channelBind.size();
    }

    @Override
    public String toString() {
        return channelBind.toString();
    }



}
