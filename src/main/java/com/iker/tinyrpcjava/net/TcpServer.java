package com.iker.tinyrpcjava.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;

@Component
public class TcpServer {
    @Getter
    @Setter
    private EventLoopGroup mainLoopGroup;       // mainReactor

    @Getter
    @Setter
    private EventLoopGroup workerLoopGroup;     // io subReactors

    @SneakyThrows(InterruptedException.class)
    public void bind(int port) {
        workerLoopGroup = new NioEventLoopGroup(4);
        mainLoopGroup = new NioEventLoopGroup(1);

        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(mainLoopGroup, workerLoopGroup)
                .option(ChannelOption.SO_BACKLOG, 128)
                .channel(NioServerSocketChannel.class);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        if (channelFuture.isSuccess()) {
            System.out.println("bind success");
            channelFuture.channel().closeFuture().sync();
        }


    }
}
