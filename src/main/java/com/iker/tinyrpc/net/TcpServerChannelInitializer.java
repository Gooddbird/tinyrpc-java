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

@ChannelHandler.Sharable
@Component
public class TcpServerChannelInitializer extends ChannelInitializer<Channel> {

    @Resource
    private TcpServerChannelInboundHandler tcpServerChannelInboundHandler;

    // notice: netty don't allow Decoder to be Sharable, because decoder can't share, you must alloc new decoder object
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(75, 75, 75, TimeUnit.SECONDS))
                .addLast("tinyPBDecoder", new TinyPBDecoder())
                .addLast("tinyPBEncoder", new TinyPBEncoder())
                .addLast("inboundHandler", tcpServerChannelInboundHandler)
                .addLast("exceptionHandler", new TcpServerExceptionHandler());
    }
}
