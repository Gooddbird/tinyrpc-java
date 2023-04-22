package com.iker.tinyrpc.net;

import com.iker.tinyrpc.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;

@SpringBootTest
@Slf4j
class TcpServerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void start() {
        try {
            TcpServer tcpServer = SpringContextUtil.getBean("tinyrpc-TcpServer", TcpServer.class);
            tcpServer.setMainLoopGroupSize(1);
            tcpServer.setWorkerLoopGroupSize(4);
            tcpServer.setLocalAddress(new InetSocketAddress(12345));
            tcpServer.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}