package com.iker.tinyrpc.net;

import com.iker.tinyrpc.proto.queryNameReq;
import com.iker.tinyrpc.protocol.TinyPBProtocol;
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
        TcpClient tcpClient = new TcpClient(eventLoopGroup);
        tcpClient.connect(new InetSocketAddress("0.0.0.0", 12345));
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
}