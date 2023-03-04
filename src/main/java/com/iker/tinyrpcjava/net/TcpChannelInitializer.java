package com.iker.tinyrpcjava.net;

import com.iker.tinyrpcjava.codec.TinyPBDecoder;
import com.iker.tinyrpcjava.codec.TinyPBEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class TcpChannelInitializer <SocketChannel> extends ChannelInitializer<Channel> {

    @Resource
    private TinyPBDecoder tinyPBDecoder;

    @Resource
    private TinyPBEncoder tinyPBEncoder;

    @Resource
    private TcpChannelInboundHandlerAdapter tcpChannelInboundHandlerAdapter;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(75, 75, 75, TimeUnit.SECONDS))
                .addLast("decoder", tinyPBDecoder)
                .addLast("encoder", tinyPBEncoder)
                .addLast("inboundHandlerAdapter", tcpChannelInboundHandlerAdapter);
    }
}
