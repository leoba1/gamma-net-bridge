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

    /**
     * 魔数4位+数据类型1位+版本号1位+总数据长度4位+元数据长度4位+元数据+消息数据(总数据长度-元数据长度)
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) {
        //写入魔数4
        out.writeBytes(MAGIC_NUM);
        //数据类型1
        out.writeByte(msg.getType());
        //版本号1
        out.writeByte(3);
        //log
//        ByteBufUtils.bufLog(out);

        //获取元数据
        String jsonStr = JSONUtil.toJsonStr(msg.getMetaData());

        int metaDataLength = jsonStr.getBytes().length;

        if (msg.getData() != null) {
            int dataLength = msg.getData().length;
            //总数据长度4
            out.writeInt(metaDataLength + dataLength + 4);//+ 4 元数据长度位4，给帧解码器用的
        } else {
            //总数据长度4
            out.writeInt(metaDataLength + 4);//+ 4 元数据长度位4
        }

        //元数据长度4
        out.writeInt(metaDataLength);
        //元数据
        out.writeBytes(jsonStr.getBytes(StandardCharsets.UTF_8));

        if (msg.getData() != null) {
            out.writeBytes(msg.getData());
        }
    }
}
