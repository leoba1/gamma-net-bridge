package com.bai.codec;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.bai.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.bai.constants.Constants.MAGIC_NUM;

/**
 * @author bzh
 * 🤪回来吧我的Java👈🏻🤣
 * Create Time:2023/6/11 17:47
 */
@Slf4j
public class MessageDecoder extends LengthFieldBasedFrameDecoder {


    /**
     * 帧解码器，消息长度不足会等待一会
     * 魔数4位+数据类型1位+版本号1位+总数据长度4位+元数据长度4位+元数据+消息数据(总数据长度-元数据长度)
     *
     * @Param maxFrameLength 最大帧长度 2MB
     * @Param lengthFieldOffset 长度字段位移量 6
     * @Param lengthFieldLength 长度字段的长度 4
     * @Param lengthAdjustment 长度调整
     * @Param initialBytesToStrip 剥离字节数
     */
    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public MessageDecoder() {
        super(2 << 20, 6, 4, 0, 0);
    }

    /**
     * 魔数4位+数据类型1位+版本号1位+总数据长度4位+元数据长度4位+元数据+消息数据(总数据长度-元数据长度)
     */
    @Override
    protected Message decode(ChannelHandlerContext ctx, ByteBuf in1) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, in1);
        Message message = new Message();
        //检查魔数,魔数只有4字节
        byte[] magic = new byte[4];
        in.readBytes(magic);
        if (!ArrayUtil.equals(magic, MAGIC_NUM)) {
            throw new IllegalArgumentException("Invalid magic number");
        }
        //获取数据类型1
        byte type = in.readByte();
        message.setType(type);

        //版本号1
        in.readByte();

        //总数据长度4
        int totalDataLength = in.readInt() - 4;
        //元数据长度4
        int metadataLength = in.readInt();

        //元数据
        byte[] metadata = new byte[metadataLength];
        if (metadataLength == 0) {
            throw new IllegalArgumentException("error metadata");
        }
        in.readBytes(metadata);
        String jsonStr = new String(metadata, StandardCharsets.UTF_8);
        Map<String, Object> metaData = JSONUtil.parseObj(jsonStr);
        message.setMetaData(metaData);

        //原本的数据
        int dataLength = totalDataLength - metadataLength;
        if (dataLength == 0) {
            return message;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        message.setData(data);
        return message;
    }
}
