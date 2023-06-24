import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TunnelClient {
    private String serverHost; // 服务器主机
    private int serverPort; // 服务器端口

    public TunnelClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast( new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast(new TunnelClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(serverHost, serverPort).sync();
            System.out.println("Connected to server: " + serverHost + ":" + serverPort);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Channel channel = future.channel();

            while (true) {
                String message = reader.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                ByteBuf buffer = channel.alloc().buffer();
                buffer.writeBytes(message.getBytes("UTF-8"));
                channel.writeAndFlush(buffer);
            }

            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String serverHost = "localhost"; // 设置服务器主机
        int serverPort = 8080; // 设置服务器端口
        TunnelClient client = new TunnelClient(serverHost, serverPort);
        client.start();
    }
}
