package net.sf.jremoterun.utilities.nonjdk.net.socketcopy.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class BridgeHandler extends ChannelInboundHandlerAdapter {
    public final TcpBridgeServer tcpBridgeServer;
    public final boolean port;

    public BridgeHandler(TcpBridgeServer tcpBridgeServer, boolean port) {
        this.tcpBridgeServer = tcpBridgeServer;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        synchronized (tcpBridgeServer.lock) {
            if (port) {
                tcpBridgeServer.port1Channel = ctx.channel();
                if (tcpBridgeServer.port2Channel != null) {
                    tcpBridgeServer.bridge(tcpBridgeServer.port1Channel, tcpBridgeServer.port2Channel);
                }
            } else {
                tcpBridgeServer.port2Channel = ctx.channel();
                if (tcpBridgeServer.port1Channel != null) {
                    tcpBridgeServer.bridge(tcpBridgeServer.port1Channel, tcpBridgeServer.port2Channel);
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Forwarding is handled by ForwardHandler
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        synchronized (tcpBridgeServer.lock) {
            if (port) {
                tcpBridgeServer.port1Channel = null;
            } else {
                tcpBridgeServer.port2Channel = null;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
