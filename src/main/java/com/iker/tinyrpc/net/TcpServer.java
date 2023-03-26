package com.iker.tinyrpc.net;

import com.iker.tinyrpc.annotation.AnnotationContextHandler;
import com.iker.tinyrpc.annotation.TinyPBService;
import com.iker.tinyrpc.net.rpc.RpcServiceFactory;
import com.iker.tinyrpc.util.SpringContextUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.Set;


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


    public void registerService() {
        AnnotationContextHandler annotationContextHandler = new AnnotationContextHandler("com.iker.tinyrpc");
        Set<Class<?>> classSet = annotationContextHandler.scanAnnotation(TinyPBService.class);
        for (Class<?> item : classSet) {
            TinyPBService annotation = item.getAnnotation(TinyPBService.class);
            String name = Optional.ofNullable(annotation).<RuntimeException>orElseThrow(
                    () -> { throw new RuntimeException("get TinyPBService annotation null"); }
            ).name();

            // register name must be same as the service's name in protobuf file
            if (name.isEmpty()) {
                name = item.getSuperclass().getSimpleName();
            }

            // 1. register service to BeanFactory
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(item);
            SpringContextUtil.getBeanFactory().registerBeanDefinition(item.getName(), beanDefinition);

            // 2. get this bean, then register to RpcServiceFactory
            SpringContextUtil.getBean("rpcServiceFactory", RpcServiceFactory.class).registerService(name, SpringContextUtil.getBean(item.getName()));

        }
    }

    public void start() throws InterruptedException {

        registerService();

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
