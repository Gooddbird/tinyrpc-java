package com.iker.tinyrpc.net;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.iker.tinyrpc.annotation.AnnotationContextHandler;
import com.iker.tinyrpc.annotation.TinyPBService;
import com.iker.tinyrpc.net.rpc.RpcServiceFactory;
import com.iker.tinyrpc.proto.*;
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
            TcpServer tcpServer = new TcpServer(new InetSocketAddress(12345), 1, 4);
            tcpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}