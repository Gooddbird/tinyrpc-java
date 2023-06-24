package com.iker.tinyrpc.net;

import com.iker.tinyrpc.net.rpc.RpcFutureMap;
import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
public class TcpClientChannelInboundHandler extends ChannelInboundHandlerAdapter {
    /**
     *
     */
    public TcpClientChannelInboundHandler() {
        super();
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channelRegistered");
        super.channelRegistered(ctx);
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
        super.channelActive(ctx);
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive{}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    /**
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("channelRead{}", ctx.channel().remoteAddress().toString());
        Optional.ofNullable((RpcProtocol) msg).ifPresent(
                (protocol) -> {
                    log.info(String.format("success get reply protocol of msgReq [%s]", protocol.getMsgReq()));
                    SpringContextUtil.getApplicationContext().getBean(RpcFutureMap.class).getFuture(protocol.getMsgReq()).invoke(protocol);
                }
        );
        super.channelRead(ctx, msg);
    }

    /**
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("it is exception");
        super.exceptionCaught(ctx, cause);
    }
}
