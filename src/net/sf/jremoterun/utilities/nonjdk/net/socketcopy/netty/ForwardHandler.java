package net.sf.jremoterun.utilities.nonjdk.net.socketcopy.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ForwardHandler extends ChannelInboundHandlerAdapter {
    public final Channel target;

    public ForwardHandler(Channel target) {
        this.target = target;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (target.isActive()) {
            target.writeAndFlush(msg);
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (target.isActive()) {
            target.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
