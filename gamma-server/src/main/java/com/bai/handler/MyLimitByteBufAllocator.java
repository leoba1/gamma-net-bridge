package com.bai.handler;
import io.netty.channel.Channel;
import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 限速管理 ByteBufAllocator
 * 实现原理  参考源码 AbstractNioByteChannel 的  public final void read() 方法
 * allocHandle 管理了 每次自动申请  byteBuf 的大小
 * 如果 guess 则会break 这一次循环
 * 如果1秒内读取的数据超过 限制的 byte 大小
 * 可以返回 0
 * 一直不读取 tcp 缓冲区就会占满  从而导致 tcp windowSize 为 0
 * 实现限速
 * ------------------------------------------------------------------------------------
 * 这段代码是一个自定义的Netty的ByteBufAllocator限速管理器。它通过控制每次读取的数据大小来实现限速的功能。
 * 首先，该类继承自DefaultMaxMessagesRecvByteBufAllocator，重写了newHandle方法，返回一个AbstractHandleImpl的实例作为Handle。
 * AbstractHandleImpl是一个抽象类，它实现了MaxMessageHandle接口，并定义了一些属性和方法。其中，limit表示限制的字节数，
 * readByteLength表示已读取的字节数，channel表示当前的通道，dynamic表示动态分配的字节数，step表示每次增加的字节数。
 * AbstractHandleImpl的具体实现有两个子类：DynamicAllocatorHandImp和StaticAllocatorHandImp。
 * DynamicAllocatorHandImp是DynamicAllocatorHandImp的实现类，它重写了guess方法。在这个方法中，首先判断channel是否为空，
 * 如果为空，说明是刚刚初始化的时候，直接返回默认的限制字节数。然后，累加读取的字节数readByteLength，并判断是否超过了限制字节数limit。
 * 如果超过了限制字节数，就调用triggerLimit方法判断是否触发限流。如果触发了限流，则返回0，表示不分配内存；
 * 否则，根据最后一次读取的字节数和动态分配的字节数，调整dynamic的值。最后，返回dynamic作为要分配的内存大小。
 * StaticAllocatorHandImp是StaticAllocatorHandImp的实现类，它也重写了guess方法。在这个方法中，首先判断channel是否为空，如果为空，
 * 说明是刚刚初始化的时候，直接返回默认的限制字节数。然后，累加读取的字节数readByteLength，并判断是否超过了限制字节数limit。
 * 如果超过了限制字节数，就调用triggerLimit方法判断是否触发限流。如果触发了限流，则返回0，表示不分配内存；否则，返回limit作为要分配的内存大小。
 * 这个限速管理器的原理是，在每次读取数据时，累加已读取的字节数，并判断是否超过了限制字节数。如果超过了限制字节数，
 * 就根据一定的规则来调整动态分配的字节数，以实现限速的效果。同时，还会记录上次触发限制的时间，以便在一定时间内不再触发限制。
 * 这样就可以控制读取的速度，避免缓冲区占满导致的问题。
 */
@Slf4j
public class MyLimitByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator {


    /**
     *  限制时间 1s
     */
    public static final AttributeKey<Long> LIMIT_TIME = AttributeKey.newInstance("LIMIT_TIME");
    public static final long TIME_1S = 1000;


    public static final int DEFAULT_LIMIT = 1024;

    private AbstractHandleImpl handle;

    @Override
    public Handle newHandle() {
        if (this.handle == null) {
            this.handle = new DynamicAllocatorHandImp(MyLimitByteBufAllocator.DEFAULT_LIMIT);
        }
        return this.handle;
    }

    public AbstractHandleImpl getHandle() {
        return handle;
    }


    public  abstract class AbstractHandleImpl extends MaxMessageHandle {
        protected   int limit;
        protected  int readByteLength = 0;
        protected  Channel channel;
        protected  int dynamic;
        protected  int step = 1024;

        public AbstractHandleImpl(int limit) {
            this.limit = limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        @Override
        public abstract int guess() ;
    }


    class DynamicAllocatorHandImp extends AbstractHandleImpl {
        public DynamicAllocatorHandImp(int limit) {
            super(limit);
        }

        @Override
        public int guess() {
            if(null == channel){
                this.dynamic = MyLimitByteBufAllocator.DEFAULT_LIMIT;
                return MyLimitByteBufAllocator.DEFAULT_LIMIT;
            }

            int lastReadByteSize = this.lastBytesRead();
            // 累加读取字节数
            this.readByteLength += lastReadByteSize;
            // 如果累加的字节数 大于限制字节数 则计算是否触发限流
            if(this.readByteLength >= this.limit){
                if (triggerLimit()) return 0;
            }
            // 如果最后一次读取的字节数 小于 上一次分配的字节数 就减少点分配
            if(lastReadByteSize < this.dynamic){
                if(this.dynamic <= MyLimitByteBufAllocator.DEFAULT_LIMIT){
                    this.dynamic = MyLimitByteBufAllocator.DEFAULT_LIMIT;
                }else{
                    int tmp = (this.dynamic-lastReadByteSize);
                    this.dynamic = this.dynamic - tmp;
                }
            }else{
                this.dynamic += step;
            }
            return  this.dynamic;
        }

        private boolean triggerLimit() {
            Long old =  channel.attr(MyLimitByteBufAllocator.LIMIT_TIME).get();
            Long now = System.currentTimeMillis();
            // 重新统计等待时间
            if(null == old || now - old >= MyLimitByteBufAllocator.TIME_1S){
                this.readByteLength = 0;
                //  此处调试用
                //  if(null != old){
                //    log.info("解除限制 wait   {} S",(now - old)/1000);
                //  }
                log.info("限速分配大小 malloc {} KB",this.limit/1024);
                channel.attr(MyLimitByteBufAllocator.LIMIT_TIME).set(now);
                return false;
            }else{
                return true;
            }
        }

    }


    class StaticAllocatorHandImp extends AbstractHandleImpl {


        public StaticAllocatorHandImp(int limit) {
            super(limit);
        }

        @Override
        public int guess() {
            int tmp = this.limit;
            // channel 为空的时候 肯定是 刚刚初始化的时候
            if(null == channel){
                return DEFAULT_LIMIT;
            }
            // 累加读取字节数
            this.readByteLength += this.lastBytesRead();
            // 如果累加的字节数 小于限制字节数 则直接返回要分配的内存大小
            if(this.readByteLength < this.limit){
                return tmp;
            }
            Long old =  channel.attr(LIMIT_TIME).get();
            Long now = System.currentTimeMillis();
            // 重新统计等待时间
            if(null == old || now - old >= TIME_1S){
                this.readByteLength = 0;
                //  此处调试用  有空指针 bug
                //  if(null != old){
                //    log.info("解除限制 wait   {} S",(now - old)/1000);
                //  }
                log.info("分配大小 malloc {} KB",this.limit/1024);
                channel.attr(LIMIT_TIME).set(now);
            }else{
                // 如果触发限制 则返回0
                tmp = 0;
            }
            return tmp;
        }
    }

}
