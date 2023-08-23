package com.bai.session;

import io.netty.channel.Channel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/23 16:39
 */
public class SessionMemory implements Session{
    private final ConcurrentHashMap<String,Channel> SC = new ConcurrentHashMap<>(6,0.8f,4);;
    private final ConcurrentHashMap<Channel,String> CS = new ConcurrentHashMap<>(6,0.8f,4);;

    @Override
    public void bind(Channel channel, String visitorId) {
        SC.put(visitorId,channel);
        CS.put(channel,visitorId);
    }

    @Override
    public void unbind(Channel channel) {
        String visitorId = CS.get(channel);
        if (visitorId == null) return;
        SC.remove(visitorId);
        CS.remove(channel);
    }

    @Override
    public void unbind(String id) {
        Channel channel = SC.get(id);
        if (channel == null) return;
        SC.remove(id);
        CS.remove(channel);
    }

    @Override
    public Channel getChannel(String id) {
        return SC.get(id);
    }

    @Override
    public String getId(Channel channel) {
        return CS.get(channel);
    }

    @Override
    public int size() {
        return SC.size();
    }



}
