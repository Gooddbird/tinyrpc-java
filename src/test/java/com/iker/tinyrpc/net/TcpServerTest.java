package com.iker.tinyrpc.net;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

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
            tcpServer.bind(12345);
            log.debug("bind success");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}