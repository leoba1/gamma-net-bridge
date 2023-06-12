package com.bai.codec;

import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import static com.bai.constants.CodecConstants.MAGIC_NUM;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/11 17:47
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //写入魔数4
        out.writeBytes(MAGIC_NUM);
        //数据类型1
        out.writeByte(msg.getType());
        //版本号1
        out.writeByte(1);
        //消息长度4
        out.writeInt(msg.getData().length);
        //消息本身
        if (msg.getData() != null){
            out.writeBytes(msg.getData());
        }

        log.debug("消息:",msg.getData());
    }
}
