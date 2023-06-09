package com.bai.message;

import java.util.Arrays;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 16:25
 */
public class MyMsg {

    /** 心跳 */
    public static final byte TYPE_HEARTBEAT = 0X00;

    /** 连接成功 */
    public static final byte TYPE_CONNECT = 0X01;

    /** 数据传输 */
    public static final byte TYPE_TRANSFER = 0X02;

    /** 连接断开 */
    public static final byte TYPE_DISCONNECT = 0X09;

    /** 数据类型 */
    private byte type;

    /** 消息传输数据 */
    private byte[] data;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MyMsg [type=" + type + ", data=" + Arrays.toString(data) + "]";
    }

}
