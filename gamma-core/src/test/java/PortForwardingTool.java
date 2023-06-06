import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class PortForwardingTool {

    private String remoteHost;
    private int remotePort;
    private int localPort;

    public PortForwardingTool(String remoteHost, int remotePort, int localPort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localPort = localPort;
    }

    public void start() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        pipeline.addLast(new ServerPortForwardingHandler());
                    }
                });

        serverBootstrap.bind(localPort).sync();

        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        pipeline.addLast(new ClientPortForwardingHandler());
                    }
                });

        Channel remoteChannel = clientBootstrap.connect(remoteHost, remotePort).sync().channel();

        remoteChannel.closeFuture().sync();
    }

    private class ServerPortForwardingHandler extends ChannelInboundHandlerAdapter {

        private Channel remoteChannel;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel localChannel = ctx.channel();

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(localChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast(new ServerDataForwardingHandler(remoteChannel));
                        }
                    });

            remoteChannel = bootstrap.connect(remoteHost, remotePort).sync().channel();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            remoteChannel.writeAndFlush(buf.retain());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            if (remoteChannel != null) {
                remoteChannel.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    private class ClientPortForwardingHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf =

 (ByteBuf) msg;
            ctx.channel().writeAndFlush(buf.retain());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    private class ServerDataForwardingHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private Channel remoteChannel;

        public ServerDataForwardingHandler(Channel remoteChannel) {
            this.remoteChannel = remoteChannel;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            remoteChannel.writeAndFlush(msg.retain());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String remoteHost = "remote-host";
        int remotePort = 1234;
        int localPort = 5678;

        PortForwardingTool tool = new PortForwardingTool(remoteHost, remotePort, localPort);
        tool.start();
    }
}
