package com.iker.tinyrpcjava.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class TcpChannelInitializer <SocketChannel> extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(75, 75, 75, TimeUnit.SECONDS))
                .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
    }
}
