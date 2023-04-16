package com.iker.tinyrpc.net.rpc;

import com.google.protobuf.*;
import com.iker.tinyrpc.net.TcpClient;
import com.iker.tinyrpc.net.TcpServer;
import com.iker.tinyrpc.protocol.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyPBErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Optional;

public class TinyPBRpcChannel implements RpcChannel {

    private final InetSocketAddress peerAddr;

    public TinyPBRpcChannel(InetSocketAddress peerAddr) {
        this.peerAddr = peerAddr;
    }

    @Override
    public void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        TinyPBProtocol sendProtocol = new TinyPBProtocol();
        TinyPBRpcController tinyPBRpcController = (TinyPBRpcController) controller;
        try {
            tinyPBRpcController.setMethodFullName(method.getFullName());
            tinyPBRpcController.setMethodName(method.getName());
            tinyPBRpcController.setPeerAddr(peerAddr);
            if (tinyPBRpcController.getMsgReq().isEmpty()) {
                tinyPBRpcController.setMsgReq("123456789");
            }

            sendProtocol.setMsgReq(tinyPBRpcController.getMsgReq());
            sendProtocol.setPbData(request.toByteString().toString("ISO-8859-1"));
            sendProtocol.setServiceName(tinyPBRpcController.getMethodFullName());
            sendProtocol.resetPackageLen();

            TcpClient tcpClient = new TcpClient(peerAddr, Optional.ofNullable(SpringContextUtil.getBean("tcpServer", TcpServer.class).getWorkerLoopGroup()).orElse(
                    new NioEventLoopGroup(1)
            ).next());

            tcpClient.connect();

            // now send data
            tcpClient.sendMessage(sendProtocol).addListener(
                    (ChannelFutureListener) future -> {
                        if (future.isSuccess()) {

                        } else {

                        }
                    }
            ).sync();

        } catch (UnsupportedEncodingException e) {
            tinyPBRpcController.setErrCode(TinyPBErrorCode.ERROR_FAILED_DESERIALIZE);
            tinyPBRpcController.setFailed(String.format("msgNo[%s] callMethod error, failed to deserialize protobuf data of request data", sendProtocol.getMsgReq()));
        } catch (TinyRpcSystemException e) {
            tinyPBRpcController.setErrCode(e.getErrorCode());
            tinyPBRpcController.setFailed(e.getErrorInfo());
        } catch (InterruptedException e) {
            tinyPBRpcController.setErrCode(TinyPBErrorCode.ERROR_FAILED_CONNECT);
            tinyPBRpcController.setFailed(String.format("msgNo[%s] callMethod error, failed to connect, occur InterruptedException exception, error text[%s]",
                    sendProtocol.getMsgReq(), e.getMessage()));
        }

    }
}
