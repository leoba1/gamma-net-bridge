package com.bai;

import com.bai.server.ReverseProxyServer;
import com.bai.server.TransportServer;

/**
 * 传输服务器，用于和客户端传输消息
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
public class ServerApp {
    public static void main(String[] args) {
        TransportServer transportServer = new TransportServer(8080);
        transportServer.start();

        ReverseProxyServer reverseProxyServer=new ReverseProxyServer();
    }
}
