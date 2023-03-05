package com.iker.tinyrpc.net;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@SpringBootTest
@Slf4j
class TcpServerTest {

    @Resource
    private TcpServer tcpServer;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void bind() {
        try {
            tcpServer.start(new InetSocketAddress(12345));
            log.debug("bind success");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}