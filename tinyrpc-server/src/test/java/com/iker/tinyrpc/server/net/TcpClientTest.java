package com.iker.tinyrpc.server.net;

import com.iker.tinyrpc.server.protocol.TinyPBProtocol;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

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
        TinyPBProtocol protocol = new TinyPBProtocol();
        protocol.setPbData("test");
        String msgReq = "1234567890";
        protocol.setMsgReq(msgReq);
        protocol.setErrCode(0);
        protocol.setErrInfo("");
        String serviceName = "TestService.query";
        protocol.setServiceName(serviceName);
        protocol.resetPackageLen();
        return protocol;
    }

    @Test
    void connect() {
        genTcpClient();
    }

    @Test
    void sendMessage() {
        TcpClient tcpClient = genTcpClient();
        TinyPBProtocol protocol = genTinyPBProtocol();
        tcpClient.sendMessage(protocol);
    }
}