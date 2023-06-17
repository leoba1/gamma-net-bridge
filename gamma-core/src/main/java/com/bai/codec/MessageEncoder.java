package com.bai.codec;

import cn.hutool.core.util.ByteUtil;
import com.bai.message.Message;
import com.bai.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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

    public MessageEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) {
        //写入魔数4
        out.writeBytes(MAGIC_NUM);
        ByteBufUtils.bufLog(out);
        //数据类型1
        out.writeByte(msg.getType());
        //版本号1
        out.writeByte(1);
        //消息长度4
        out.writeInt(msg.getData().length);
        ByteBufUtils.bufLog(out);

        try {
            // 消息本身
            if (msg.getData() != null) {
                out.writeBytes(msg.getData());
            }
        } catch (Exception e) {
            log.debug("",e);
        }

        ByteBufUtils.bufLog(out);
        log.debug("消息:",msg.getData());
    }
}
