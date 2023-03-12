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
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;


@Slf4j
public class TcpServer {
    @Getter
    private final EventLoopGroup mainLoopGroup;       // mainReactor

    @Getter
    private final EventLoopGroup workerLoopGroup;     // io subReactors

    @Getter
    private final InetSocketAddress localAddress;

    public TcpServer(InetSocketAddress address, int mainLoopGroupSize, int workerLoopGroupSize) {
        localAddress = address;
        mainLoopGroup = new NioEventLoopGroup(mainLoopGroupSize);
        workerLoopGroup = new NioEventLoopGroup(workerLoopGroupSize);
    }

    public void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(mainLoopGroup, workerLoopGroup)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TcpServerChannelInitializer())
                .localAddress(localAddress);

        ChannelFuture channelFuture = serverBootstrap.bind().sync();
        channelFuture.addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                InetSocketAddress address = (InetSocketAddress) future.channel().localAddress();
                assert (address != null);
                log.info(String.format("TinyRPC TcpServer start success, listen on [%s:%d]", getLocalAddress().getHostString(), getLocalAddress().getPort()));
            } else {
                log.error("TinyRPC TcpServer start error");
                future.cause().printStackTrace();
                throw new RuntimeException(future.cause().getMessage());
            }
        });

        // wait until close this channel
        channelFuture.channel().closeFuture().sync();
        log.info("TinyRPC quit success");

        mainLoopGroup.shutdownGracefully().sync();
        workerLoopGroup.shutdownGracefully().sync();
    }


}
