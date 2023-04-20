package com.iker.tinyrpc.net.rpc;

import com.google.protobuf.*;
import com.iker.tinyrpc.net.TcpClient;
import com.iker.tinyrpc.net.TcpServer;
import com.iker.tinyrpc.protocol.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyPBErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Optional;

public abstract class TinyPBRpcChannel implements RpcChannel {
    protected final InetSocketAddress peerAddr;

    protected TinyPBProtocol sendProtocol;

    protected TinyPBRpcController tinyPBRpcController;

    protected TcpClient tcpClient;


    protected TinyPBRpcChannel(InetSocketAddress peerAddr) {
        this.peerAddr = peerAddr;
    }

    @Override
    public void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        try {

            tinyPBRpcController = fillRpcController(method, controller);
            sendProtocol = generateTinyPBProtocol(request, tinyPBRpcController);

            // do async connect
            ioHandler();

        } catch (UnsupportedEncodingException e) {
            tinyPBRpcController.setErrCode(TinyPBErrorCode.ERROR_FAILED_DESERIALIZE);
            tinyPBRpcController.setFailed(String.format("msgNo[%s] callMethod error, failed to deserialize protobuf data of request data", sendProtocol.getMsgReq()));
        } catch (TinyRpcSystemException e) {
            assert tinyPBRpcController != null;
            tinyPBRpcController.setErrCode(e.getErrorCode());
            tinyPBRpcController.setFailed(e.getErrorInfo());
        } catch (InterruptedException e) {
            tinyPBRpcController.setErrCode(TinyPBErrorCode.ERROR_FAILED_CONNECT);
            tinyPBRpcController.setFailed(String.format("msgNo[%s] callMethod error, failed to connect, occur InterruptedException exception, error text[%s]",
                    sendProtocol.getMsgReq(), e.getMessage()));
        }
    }

    protected TinyPBRpcController fillRpcController(Descriptors.MethodDescriptor method, RpcController controller) {
        TinyPBRpcController tinyPBRpcController = (TinyPBRpcController) controller;
        tinyPBRpcController.setMethodFullName(method.getFullName());
        tinyPBRpcController.setMethodName(method.getName());
        tinyPBRpcController.setPeerAddr(peerAddr);
        if (tinyPBRpcController.getMsgReq().isEmpty()) {
            tinyPBRpcController.setMsgReq("123456789");
        }

        return tinyPBRpcController;
    }

    protected TinyPBProtocol generateTinyPBProtocol(Message request, TinyPBRpcController tinyPBRpcController) throws UnsupportedEncodingException {
        TinyPBProtocol sendProtocol = new TinyPBProtocol();
        sendProtocol.setMsgReq(tinyPBRpcController.getMsgReq());
        sendProtocol.setPbData(request.toByteString().toString("ISO-8859-1"));
        sendProtocol.setServiceName(tinyPBRpcController.getMethodFullName());
        sendProtocol.resetPackageLen();

        return sendProtocol;
    }

    protected TcpClient asyncConnect() throws InterruptedException {
        TcpClient tcpClient = new TcpClient(peerAddr, Optional.ofNullable(SpringContextUtil.getBean("tcpServer", TcpServer.class).getWorkerLoopGroup()).orElse(
                new NioEventLoopGroup(1)
        ).next());

        tcpClient.connect();
        return tcpClient;
    }


    /**
     * send protocol and get reply at there, you can do it by yourself, such as:
     * 1. sync: send data, then blocking wait until get peer server's reply
     * 2. async: send data, and set callback, then do other things. callback will run when get peer server's reply
     */
    protected abstract void ioHandler() throws InterruptedException;

}
