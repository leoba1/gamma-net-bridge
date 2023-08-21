package com.bai.codec;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.bai.message.Message;
import com.bai.utils.ByteBufUtils;
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
    protected Message decode(ChannelHandlerContext ctx, ByteBuf in) {
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

        //元数据
        int metadataLength = in.readInt();
        if (metadataLength!=0){
            byte[] metadata=new byte[metadataLength];
            in.readBytes(metadata);
            String jsonStr = new String(metadata, StandardCharsets.UTF_8);
            Map<String, Object> metaData = JSONUtil.parseObj(jsonStr);
            message.setMetaData(metaData);
        }

        //原本的数据
        if (in.readInt() == 0) {
            return message;
        }
        int dataLength = in.readInt();
        byte[] bytes = new byte[dataLength];

        in.readBytes(bytes);
        message.setData(bytes);

//        System.out.println("接受的消息:");
//        ByteBufUtils.bufLog(buf);
        //message消息
        return message;
    }
}
