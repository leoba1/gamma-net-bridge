package com.bai.codec;

import cn.hutool.core.util.ArrayUtil;
import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.bai.constants.CodecConstants.MAGIC_NUM;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/11 17:47
 */
@Slf4j
public class MessageDecoder extends LengthFieldBasedFrameDecoder {



    /**
     * 帧解码器，消息长度不足会等待一会
     *  @Param maxFrameLength 最大帧长度
     *  @Param lengthFieldOffset 长度字段位移量
     *  @Param lengthFieldLength 长度字段的长度
     *  @Param lengthAdjustment 长度调整
     *  @Param initialBytesToStrip 剥离字节数
     * 1024,8,4,0,0
     */
    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public MessageDecoder() {
        super(Integer.MAX_VALUE, 6, 4, 0, 0);
    }

    @Override
    protected Message decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Message message=new Message();
        //检查魔数,魔数只有4字节
        byte[] magic = new byte[4];
        in.readBytes(magic);
        if (!ArrayUtil.equals(magic,MAGIC_NUM)){
            throw new IllegalArgumentException("Invalid magic number");
        }
        //获取数据类型
        byte type = in.readByte();
        message.setType(type);

        //版本号
        in.readByte();

        //获取消息长度
        int length = in.readInt();

        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        message.setData(bytes);

        //message消息
        return message;
    }
}
