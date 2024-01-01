package com.iker.tinyrpc.server.net;


import com.iker.tinyrpc.server.codec.TinyPBDecoder;
import com.iker.tinyrpc.server.codec.TinyPBEncoder;
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
                .addLast("tinyPBEncoder", new TinyPBEncoder())
                .addLast("inboundHandler", new TcpServerChannelInboundHandler())
                .addLast("exceptionHandler", new TcpServerExceptionHandler());
    }
}
