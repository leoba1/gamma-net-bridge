package com.bai.codec;

import com.bai.message.Message;
import com.bai.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static com.bai.constants.Constants.MAGIC_NUM;

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
        //数据类型1
        out.writeByte(msg.getType());
        //版本号1
        out.writeByte(2);
        //log
        ByteBufUtils.bufLog(out);

        if (msg.getMetaData() == null || msg.getMetaData().isEmpty()) {
            //元数据长度4
            out.writeInt(0);
        } else {
            String jsonStr = JSONUtil.toJsonStr(msg.getMetaData());
            //元数据长度4
            out.writeInt(jsonStr.getBytes().length);
            //元数据
            if (jsonStr.getBytes().length > 0) {
                out.writeBytes(jsonStr.getBytes(StandardCharsets.UTF_8));
            }
        }
        //log
        ByteBufUtils.bufLog(out);
        if (msg.getData() == null || msg.getData().length == 0) {
            //消息长度4
            out.writeInt(0);
        } else {
            //消息长度4
            out.writeInt(msg.getData().length);
            // 消息本身
            if (msg.getData() != null) {
                out.writeBytes(msg.getData());
            }
        }
        //log
        ByteBufUtils.bufLog(out);
    }
}
