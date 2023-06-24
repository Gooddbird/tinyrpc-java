package com.iker.tinyrpc.net;

import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Optional;

import static com.iker.tinyrpc.util.TinyRpcErrorCode.ERROR_FAILED_CONNECT;

@Slf4j
public class TcpClient {

    @Getter
    private InetSocketAddress peerAddress;

    @Getter
    private InetSocketAddress localAddress;

    @Getter
    private final EventLoopGroup eventLoopGroup;

    @Getter
    private Channel channel;

    private ChannelFuture connectChannelFuture;

    private final Bootstrap bootstrap;

    public TcpClient(InetSocketAddress peerAddress, EventLoopGroup eventLoopGroup) {
        this.peerAddress = peerAddress;
        this.eventLoopGroup = eventLoopGroup;
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(Optional.ofNullable(peerAddress).orElseThrow(
                        () -> { throw new TinyRpcSystemException(ERROR_FAILED_CONNECT, "peer address is null"); }
                ))
                .handler(new TcpClientChannelInitializer());
    }

    public ChannelFuture connect() {
        connectChannelFuture = bootstrap.connect();
        channel = connectChannelFuture.channel();
        connectChannelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                peerAddress = (InetSocketAddress) future.channel().remoteAddress();
                localAddress = (InetSocketAddress) future.channel().localAddress();
                log.info(String.format("success connect to remoteAddr[%s:%d], localAddr[%s:%d]",
                        peerAddress.getHostName(), peerAddress.getPort(),
                        localAddress.getHostName(), localAddress.getPort()));
            } else {
                log.error(String.format("connect failed, peer addr[%s:%d]", peerAddress.getHostName(), peerAddress.getPort()));
                log.error("exception: ", future.cause());
            }
        });
        return connectChannelFuture;
    }


    public ChannelFuture sendMessage(RpcProtocol protocol) {
        if (!connectChannelFuture.isDone()) {
            try {
                connectChannelFuture.sync();
            } catch (InterruptedException e) {
                log.error("exception ", e);
                throw new TinyRpcSystemException(e.getMessage());
            }
        }
        if (!channel.isActive()) {
            throw new TinyRpcSystemException(ERROR_FAILED_CONNECT, String.format("sendMessage error, connection[%s:%d] is not active", peerAddress.getHostName(), peerAddress.getPort()));
        }
        return channel.writeAndFlush(protocol).addListener( future -> {
            if (future.isSuccess()) {
                log.info(String.format("success send protocol message[%s] to remoteAddr[%s:%d]", protocol.getMsgReq(), peerAddress.getHostName(), peerAddress.getPort()));
            } else {
                log.error(String.format("failed send protocol message[%s] to remoteAddr[%s:%d]", protocol.getMsgReq(), peerAddress.getHostName(), peerAddress.getPort()));
                log.error("exception: ", future.cause());
            }
        });
    }

    public void awaitResponseWithTimeout(String msgReq, int timeout) throws InterruptedException {
//        channel.
    }

}
