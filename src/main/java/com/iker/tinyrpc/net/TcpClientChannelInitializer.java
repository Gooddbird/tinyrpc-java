package com.iker.tinyrpc.net;

import com.iker.tinyrpc.codec.TinyPBDecoder;
import com.iker.tinyrpc.codec.TinyPBEncoder;
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
                .addLast("decoder", new TinyPBDecoder())
                .addLast("encoder", new TinyPBEncoder())
                .addLast("outboundHandlerAdapter", new TcpClientChannelInboundHandler());
    }
}
