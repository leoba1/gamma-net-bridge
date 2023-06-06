要实现一个双向的内网穿透工具，需要同时编写客户端和服务器端的代码。下面是一个基本的示例，展示了如何使用 Netty 框架编写一个简单的双向内网穿透工具。

```java
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
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
```

在上述代码示例中，我们创建了一个 `PortForwardingTool` 类，它既包括服务器端的功能，也包括客户端的功能。

服务器端使用 `ServerBootstrap` 创建一个服务器监听器，等待来自本地主机的连接。当有连接建立时，我们为连接创建一个本地 Channel，并使用另一个 `Bootstrap` 实例将其连接到远程主机。通过 `ServerDataForwardingHandler` 处理器，我们将来自本地主机的数据转发给远程主机。

客户端使用 `Bootstrap` 创建一个客户端 Channel，并连接到远程主机。来自远程主机的数据将通过 `ClientPortForwardingHandler` 处理器转发给本地主机。

在 `main()` 方法中，我们创建了一个 `PortForwardingTool` 实例，并调用 `start()` 方法来启动双向内网穿透工具。

请注意，这只是一个基本的示例，可能不适用于所有情况。在实际使用中，你可能需要添加更多的功能和错误处理来满足你的需求。此外，内网穿透涉及到更多的网络安全问题，请确保你了解并采取适当的安全措施来保护你的网络和系统。
