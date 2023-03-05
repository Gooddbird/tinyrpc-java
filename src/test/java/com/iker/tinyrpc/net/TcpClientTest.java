package com.iker.tinyrpc.net;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TcpClientTest {

    @Resource
    private TcpClient tcpClient;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void connect() {
        try {
            tcpClient.connect("127.0.0.1", 12345);
            log.debug("connect success");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}