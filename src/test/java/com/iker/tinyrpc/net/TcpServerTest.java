package com.iker.tinyrpc.net;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.iker.tinyrpc.net.rpc.RpcServiceFactory;
import com.iker.tinyrpc.proto.*;
import com.iker.tinyrpc.proto.QueryService;
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
            QueryServiceHandler queryServiceHandler = new QueryServiceHandler();
            SpringContextUtil.getBean("rpcServiceFactory", RpcServiceFactory.class).registerService(queryServiceHandler);
            tcpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
@Slf4j
class QueryServiceHandler extends com.iker.tinyrpc.proto.QueryService {

    @Override
    public void queryName(RpcController controller, queryNameReq request, RpcCallback<queryNameRes> done) {
        queryNameRes response = queryNameRes.newBuilder().build();
        log.info("call method queryName successful");
        done.run(response);
    }

    @Override
    public void queryAge(RpcController controller, queryAgeReq request, RpcCallback<queryAgeRes> done) {
        queryAgeRes response = queryAgeRes.newBuilder().build();
        log.info("call method queryAge successful");
        done.run(response);
    }
};