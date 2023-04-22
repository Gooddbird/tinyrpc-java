package com.iker.tinyrpc.net;

import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBDecoder;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class TcpClientChannelInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(75, 75, 75, TimeUnit.SECONDS))
                .addLast("tinyPBDecoder", new TinyPBDecoder())
                .addLast("inBoundHandler", new TcpClientChannelInboundHandler())
                .addLast("tinyPBEncoder", new TinyPBEncoder())
                .addLast("outBoundHandler", new TcpClientChannelOutboundHandler())
                .addLast("exceptionHandler", new TcpServerExceptionHandler());
    }
}
