package net.sf.jremoterun.utilities.nonjdk.net.socketcopy.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

public class TcpBridgeServer {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public Channel port1Channel;
    public Channel port2Channel;
    public final Object lock = new Object();



    public void start(int port1, int port2) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b1 = new ServerBootstrap();
            b1.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(createBridgeHandler(true));

            ChannelFuture f1 = b1.bind(port1).sync();

            ServerBootstrap b2 = new ServerBootstrap();
            b2.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(createBridgeHandler(false));

            ChannelFuture f2 = b2.bind(port2).sync();

            System.out.println("Bridge server started on ports " + port1 + " and " + port2);
            f1.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public BridgeHandler createBridgeHandler(boolean port) {
        return new BridgeHandler(this, true);
    }


    public String channelNameJrr = "jrr1";

    public void bridge(Channel c1, Channel c2) {
//        c1.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
//        c2.pipeline().addLast(new LoggingHandler(LogLevel.INFO));

        if (true) {
            ChannelHandler jrr1 = c1.pipeline().get(channelNameJrr);
            if (jrr1 != null) {
                log.info("remove from ch1");
                c1.pipeline().remove(jrr1);
            }
        }
        if (true) {
            ChannelHandler jrr1 = c2.pipeline().get(channelNameJrr);
            if (jrr1 != null) {
                log.info("remove from ch2");
                c2.pipeline().remove(jrr1);
            }
        }
        c1.pipeline().addLast(channelNameJrr, createForwardHandler(c2));
        c2.pipeline().addLast(channelNameJrr, createForwardHandler(c1));
        System.out.println("Bridged connections between port1 and port2");
    }

    public ForwardHandler createForwardHandler(Channel ch){
        return new ForwardHandler(ch);
    }


//    public static void main(String[] args) throws InterruptedException {
//        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
//        new TcpBridgeServer().start(8000, 9000);
//    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.err.println("Usage: java TcpBridgeServer <port1> <port2>");
            System.exit(1);
        }

        int port1 = Integer.parseInt(args[0]);
        int port2 = Integer.parseInt(args[1]);

        new TcpBridgeServer().start(port1, port2);
    }
}