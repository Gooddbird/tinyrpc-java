package com.iker.tinyrpc.net;

import com.iker.tinyrpc.codec.TinyPBDecoder;
import com.iker.tinyrpc.codec.TinyPBEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@ChannelHandler.Sharable
public class TcpClientChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {

    @Resource
    private TinyPBEncoder tinyPBEncoder;

    @Resource
    private TcpClientChannelInboundHandlerAdapter tcpClientChannelInboundHandlerAdapter;
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(75, 75, 75, TimeUnit.SECONDS))
                .addLast("decoder", new TinyPBDecoder())
                .addLast("encoder", tinyPBEncoder)
                .addLast("inboundHandlerAdapter", tcpClientChannelInboundHandlerAdapter);
    }
}