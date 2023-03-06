package com.iker.tinyrpc.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TcpClientTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void connect() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        TcpClient tcpClient = new TcpClient(eventLoopGroup);
        tcpClient.connect(new InetSocketAddress("0.0.0.0", 12345));
    }
}