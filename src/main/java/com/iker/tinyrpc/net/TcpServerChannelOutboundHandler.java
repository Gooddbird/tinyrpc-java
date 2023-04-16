package com.iker.tinyrpc.net;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpServerChannelOutboundHandler extends ChannelOutboundHandlerAdapter {
    public TcpServerChannelOutboundHandler() {
        super();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

}
