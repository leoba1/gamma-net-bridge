package com.bai;


import com.bai.client.RealProxyClient;
import com.bai.client.TransportClient;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 14:03
 */
public class ClientApp {


    public static void main(String[] args) {
        TransportClient transportClient = new TransportClient("localhost", 8080);
        transportClient.start();

        RealProxyClient realClientApp =new RealProxyClient("localhost",9090);
//        realClientApp.start();
    }
}
