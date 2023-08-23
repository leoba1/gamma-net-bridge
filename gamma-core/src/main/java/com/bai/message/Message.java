package com.bai.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;


/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/7 16:25
 */
@Data
@ToString
public class Message {

    /** 心跳 */
    public static final byte TYPE_HEARTBEAT = 0X00;

    /** 数据传输 */
    public static final byte TYPE_TRANSFER = 0X02;

    /** 连接断开 */
    public static final byte TYPE_DISCONNECT = 0X09;

    /** 通用异常信息 */
    public static final byte TYPE_ERROR = 0x06;

    /** 注册请求 */
    public static final byte REG = 0x03;

    /** 注册确认 */
    public static final byte CONFIRM = 0x04;

    /** 元数据(记录数据来源) */
    @Getter
    @Setter
    private Map<String,Object> metaData;

    /** 数据类型 */
    @Getter
    @Setter
    private byte type;

    /** 消息传输数据 */
    @Getter
    @Setter
    private byte[] data;

}
