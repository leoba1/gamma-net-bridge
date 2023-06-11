package com.bai.codec;


import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Arrays;

import static com.bai.constants.CodecConstants.MAGIC_NUM;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/11 17:47
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
                               int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Message decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
//        Message message=new Message
        //检查魔数,魔数只有4字节
        byte[] magic = new byte[4];
        in.readBytes(magic);
        if (!Arrays.equals(magic,MAGIC_NUM)){
            throw new IllegalArgumentException("Invalid magic number");
        }
        //获取数据类型
        byte type = in.readByte();


        //获取消息长度
        int length = in.readInt();

        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        //
        return null;
    }
}
