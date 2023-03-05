package com.iker.tinyrpc.net;

import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


@Slf4j
public class TcpClient {

    @Getter
    private InetSocketAddress peerAddress;

    @Getter
    private final EventLoopGroup eventLoopGroup;

    public TcpClient(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
    }

    public TcpClient(InetSocketAddress peerAddress, EventLoopGroup eventLoopGroup) {
        this.peerAddress = peerAddress;
        this.eventLoopGroup = eventLoopGroup;
    }

    public void connect() throws InterruptedException, TinyRpcSystemException {
        if (this.peerAddress == null) {
            throw new TinyRpcSystemException("connect failed, not set peerAddress");
        }
        connect(this.peerAddress);
    }

    public void connect(InetSocketAddress peerAddress) throws InterruptedException, TinyRpcSystemException {
        if (this.peerAddress != null) {
            throw new TinyRpcSystemException("init client failed, peerAddress has already set");
        }
        this.peerAddress = peerAddress;

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(peerAddress)
                .handler(new TcpClientChannelInitializer());

        try {
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    InetSocketAddress address = (InetSocketAddress) future.channel().remoteAddress();
                    assert (address != null);
                    log.debug(String.format("success connect to [%s:%d]", address.getHostName(), address.getPort()));
                } else {
                    log.error("connect failed");
                    future.cause().printStackTrace();
                    throw new TinyRpcSystemException(future.cause().getMessage());
                }
            });
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }

    }
}
