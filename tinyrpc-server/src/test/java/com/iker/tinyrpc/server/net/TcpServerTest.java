package com.iker.tinyrpc.server.net;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

@Slf4j
class TcpServerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void bind() {
        try {
            log.debug("begin success");

            TcpServer tcpServer = new TcpServer();
            tcpServer.setLocalAddress(new InetSocketAddress(12346));
            tcpServer.initMainLoopGroup(1);
            tcpServer.initWorkerLoopGroup(2);
            tcpServer.start();
            log.debug("bind success");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}