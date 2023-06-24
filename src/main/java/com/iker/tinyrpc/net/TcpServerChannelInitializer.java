package com.iker.tinyrpc.net;

import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBDecoder;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class TcpServerChannelInitializer extends ChannelInitializer<Channel> {

    // notice: netty don't allow Decoder to be Sharable, because decoder can't share, you must alloc new decoder object
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(75, 75, 75, TimeUnit.SECONDS))
                .addLast("tinyPBDecoder", new TinyPBDecoder())
                .addLast("inBoundHandler", new TcpServerChannelInboundHandler())
                .addLast("tinyPBEncoder", new TinyPBEncoder())
                .addLast("outBoundHandler", new TcpServerChannelOutboundHandler())
                .addLast("exceptionHandler", new TcpServerExceptionHandler());
    }
}
