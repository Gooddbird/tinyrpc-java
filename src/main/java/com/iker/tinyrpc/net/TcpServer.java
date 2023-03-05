package com.iker.tinyrpc.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class TcpServer {
    @Getter
    @Setter
    private EventLoopGroup mainLoopGroup;       // mainReactor

    @Getter
    @Setter
    private EventLoopGroup workerLoopGroup;     // io subReactors

    @Resource
    private TcpServerChannelInitializer tcpServerChannelInitializer;

    public void bind(int port) throws InterruptedException {

        workerLoopGroup = new NioEventLoopGroup(4);
        mainLoopGroup = new NioEventLoopGroup(1);

        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(mainLoopGroup, workerLoopGroup)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(NioServerSocketChannel.class)
                .childHandler(tcpServerChannelInitializer);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        if (channelFuture.isSuccess()) {
            System.out.println("bind success");
            channelFuture.channel().closeFuture().sync();
        }

        mainLoopGroup.shutdownGracefully().sync();
        workerLoopGroup.shutdownGracefully().sync();

    }
}
