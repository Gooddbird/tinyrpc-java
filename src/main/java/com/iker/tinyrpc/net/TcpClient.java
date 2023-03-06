package com.iker.tinyrpc.net;

import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.ConnectException;
import java.net.InetSocketAddress;

@Slf4j
public class TcpClient {

    @Getter
    private InetSocketAddress peerAddress;

    @Getter
    private final EventLoopGroup eventLoopGroup;

    private Channel channel;

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

    public void connect(InetSocketAddress peerAddress) throws TinyRpcSystemException {
        if (this.peerAddress != null) {
            throw new TinyRpcSystemException("init client failed, peerAddress has already set");
        }
        this.peerAddress = peerAddress;

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .remoteAddress(peerAddress)
                .handler(new TcpClientChannelInitializer());

        ChannelFuture channelFuture = bootstrap.connect();
        channel = channelFuture.channel();
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                InetSocketAddress remoteAddress = (InetSocketAddress) future.channel().remoteAddress();
                InetSocketAddress localAddress = (InetSocketAddress) future.channel().localAddress();
                assert (remoteAddress != null);
                log.info(String.format("success connect to remoteAddr[%s:%d], localAddr[%s:%d]",
                        remoteAddress.getHostName(), remoteAddress.getPort(),
                        localAddress.getHostName(), localAddress.getPort()));
            } else {
                log.error("connect failed, stack info:");
                future.cause().printStackTrace();
                throw new TinyRpcSystemException(future.cause().getMessage());
            }
        });
    }


//    public ChannelFuture sendMessage() {
//        if (channel.isActive()) {
//            channel.writeAndFlush();
//        }
//    }


}
