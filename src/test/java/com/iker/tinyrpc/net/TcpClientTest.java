package com.iker.tinyrpc.net;

import com.iker.tinyrpc.net.rpc.protobuf.TinyRpcController;
import com.iker.tinyrpc.net.rpc.protobuf.TinyRpcSyncChannel;
import com.iker.tinyrpc.proto.QueryService;
import com.iker.tinyrpc.proto.queryNameReq;
import com.iker.tinyrpc.proto.queryNameRes;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBProtocol;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

@SpringBootTest
@Slf4j
class TcpClientTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    TcpClient genTcpClient() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        TcpClient tcpClient = new TcpClient(new InetSocketAddress("0.0.0.0", 12345), eventLoopGroup);
        tcpClient.connect();
        return tcpClient;
    }

    TinyPBProtocol genTinyPBProtocol() {
        queryNameReq request = queryNameReq.newBuilder().setReqNo(999).setId(1).build();

        TinyPBProtocol protocol = new TinyPBProtocol();
        try {
            protocol.setPbData(request.toByteString().toString("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        };
        String msgReq = "1234567890";
        protocol.setMsgReq(msgReq);
        String serviceName = "QueryService.query_name";
        protocol.setServiceName(serviceName);
        protocol.resetPackageLen();
        log.info(String.format("package length is %d", protocol.getPkLen()));
        return protocol;
    }

    @Test
    void connect() {
        genTcpClient();
    }

    @Test
    void sendMessage() throws InterruptedException {
        TcpClient tcpClient = genTcpClient();
        TinyPBProtocol protocol = genTinyPBProtocol();
        tcpClient.sendMessage(protocol);

        Thread.sleep(100000);
    }

    @Test
    void callMethod() {
        queryNameReq request = queryNameReq.newBuilder().setReqNo(999).setId(1).build();
        queryNameRes response = queryNameRes.newBuilder().build();
        TinyRpcSyncChannel tinyRpcSyncChannel = new TinyRpcSyncChannel(new InetSocketAddress("0.0.0.0", 12345));
        TinyRpcController rpcController = new TinyRpcController();


        log.info(String.format("request info : %s", request));
        tinyRpcSyncChannel.callMethod(QueryService.getDescriptor().findMethodByName("query_name"), rpcController, request, response, null);

        log.info(String.format("get response info : %s", response));
    }
}