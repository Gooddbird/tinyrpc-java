package com.iker.tinyrpc.net;

import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;


@Slf4j
public class TcpServer {
    @Getter
    @Setter
    private EventLoopGroup mainLoopGroup;       // mainReactor

    @Getter
    @Setter
    private EventLoopGroup workerLoopGroup;     // io subReactors

    @Getter
    private InetSocketAddress localAddress;

    public void start(InetSocketAddress localAddress) throws InterruptedException, TinyRpcSystemException {
        try {
            if(this.localAddress != null) {
                throw new TinyRpcSystemException("TinyRPC TcpServer start error, local address has set.");
            }
            this.localAddress = localAddress;

            workerLoopGroup = new NioEventLoopGroup(4);
            mainLoopGroup = new NioEventLoopGroup(1);

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(mainLoopGroup, workerLoopGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new TcpServerChannelInitializer())
                    .localAddress(localAddress);

            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    InetSocketAddress address = (InetSocketAddress)future.channel().localAddress();
                    assert (address != null);
                    log.info(String.format("TinyRPC TcpServer start success, listen on [%s:%d]", getLocalAddress().getHostString(),  getLocalAddress().getPort()));
                } else {
                    log.error("TinyRPC TcpServer start error");
                    future.cause().printStackTrace();
                    throw new TinyRpcSystemException(future.cause().getMessage());
                }
            });

            // wait until close this channel
            channelFuture.channel().closeFuture().sync();
            log.info("TinyRPC quit success");
        } catch (InterruptedException e) {
            throw e;
        } finally {
            mainLoopGroup.shutdownGracefully().sync();
            workerLoopGroup.shutdownGracefully().sync();
        }

    }
}
