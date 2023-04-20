package com.iker.tinyrpc.net;

import com.iker.tinyrpc.protocol.AbstractProtocol;
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

import static com.iker.tinyrpc.util.TinyPBErrorCode.ERROR_FAILED_CONNECT;

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

        connectChannelFuture = bootstrap.connect();
        channel = connectChannelFuture.channel();
        connectChannelFuture.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                this.peerAddress = (InetSocketAddress) future.channel().remoteAddress();
                this.localAddress = (InetSocketAddress) future.channel().localAddress();
                log.info(String.format("success connect to remoteAddr[%s:%d], localAddr[%s:%d]",
                        this.peerAddress.getHostName(), this.peerAddress.getPort(),
                        this.localAddress.getHostName(), this.localAddress.getPort()));
            } else {
                log.error("connect failed, stack info:");
                future.cause().printStackTrace();
                throw new TinyRpcSystemException(future.cause().getMessage());
            }
        });
    }


    public ChannelFuture sendMessage(AbstractProtocol protocol) {
        if (!connectChannelFuture.isDone()) {
            try {
                connectChannelFuture.sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!channel.isActive()) {
            throw new TinyRpcSystemException(ERROR_FAILED_CONNECT, "sendMessage error, connection is not active");
        }
        return channel.writeAndFlush(protocol);
    }

    public void awaitResponseWithTimeout(String msgReq) throws InterruptedException {
        channel.newPromise().sync();
    }

}
