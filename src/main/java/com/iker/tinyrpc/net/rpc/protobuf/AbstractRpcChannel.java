package com.iker.tinyrpc.net.rpc.protobuf;

import com.google.protobuf.*;
import com.iker.tinyrpc.net.TcpClient;
import com.iker.tinyrpc.net.TcpServer;
import com.iker.tinyrpc.net.future.RpcSyncFuture;
import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyRpcErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
public abstract class AbstractRpcChannel implements DefaultRpcChannel {
    protected final InetSocketAddress peerAddr;

    protected TinyPBProtocol sendProtocol;

    protected TinyPBProtocol replyProtocol;

    protected TinyRpcController tinyRpcController;

    protected TcpClient tcpClient;

    protected RpcCallback<Message> done;

    protected Message request;

    protected Message responsePrototype;

    @Getter
    protected RpcSyncFuture replyFuture;

    public AbstractRpcChannel(InetSocketAddress peerAddr) {
        this.peerAddr = peerAddr;
    }


    @Override
    public void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        try {
            init(controller, request, responsePrototype, done);

            fillRpcController(method);

            generateTinyPBProtocol(request);


            ioHandler();

        } catch (TinyRpcSystemException e) {
            log.error(String.format("call rpc occur TinyRpcSystemException, error code [%d], error info [%s]", e.getErrorCode().ordinal(), e.getErrorInfo()));
            log.error("exception: ", e);
            tinyRpcController.setErrCode(e.getErrorCode());
            tinyRpcController.setFailed(e.getErrorInfo());
            callBack();
        }
//        } catch (InvalidProtocolBufferException e) {
//            log.error(String.format("call rpc occur InvalidProtocolBufferException, error info [%s]", e.getMessage()));
//            log.error("exception: ", e);
//            tinyRpcController.setErrCode(TinyRpcErrorCode.ERROR_FAILED_DESERIALIZE);
//            tinyRpcController.setFailed("failed to deserialize response protobuf data");
//            callBack();
//        }
    }

    private void init(RpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        this.request = request;
        this.responsePrototype = responsePrototype;
        this.done = done;
        tinyRpcController = (TinyRpcController) controller;
    }

    protected void fillRpcController(Descriptors.MethodDescriptor method) {

        tinyRpcController.setMethodFullName(method.getFullName());
        tinyRpcController.setMethodName(method.getName());
        tinyRpcController.setPeerAddr(peerAddr);

        if (tinyRpcController.getMsgReq().isEmpty()) {
            tinyRpcController.setMsgReq("123456789");
        }

    }

    protected Message parseResponse(Message responsePrototype) {
        try {
            RpcProtocol protocol =  replyFuture.get();
            if (protocol != null) {
                TinyPBProtocol tinyPBProtocol = (TinyPBProtocol) (protocol);
                return responsePrototype.toBuilder().mergeFrom(ByteString.copyFrom(tinyPBProtocol.getPbData(), StandardCharsets.ISO_8859_1)).build();
            }
        } catch (InterruptedException | ExecutionException | InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    protected void generateTinyPBProtocol(Message request){

        sendProtocol = new TinyPBProtocol();

        sendProtocol.setMsgReq(tinyRpcController.getMsgReq());
        try {
            sendProtocol.setPbData(request.toByteString().toString("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new TinyRpcSystemException(TinyRpcErrorCode.ERROR_FAILED_DESERIALIZE, "failed to deserialize request data");
        }
        sendProtocol.setServiceName(tinyRpcController.getMethodFullName());
        sendProtocol.resetPackageLen();

        replyProtocol = new TinyPBProtocol();
        replyProtocol.setMsgReq(sendProtocol.getMsgReq());
        replyProtocol.setServiceName(sendProtocol.getServiceName());

    }

    protected ChannelFuture asyncConnect() {
        tcpClient = new TcpClient(peerAddr, Optional.ofNullable(SpringContextUtil.getBean(TcpServer.class).getWorkerLoopGroup()).orElse(
                new NioEventLoopGroup(1)
        ).next());

        return tcpClient.connect();
    }

    protected ChannelFuture asyncSendMessage() {
        return tcpClient.sendMessage(sendProtocol);
    }

    protected void setError(TinyRpcErrorCode errorCode, String errorInfo) {
        tinyRpcController.setErrCode(errorCode);
        tinyRpcController.setErrInfo(errorInfo);
        tinyRpcController.setFailed(errorInfo);
    }

    protected void callBack() {
        if (done != null) {
            log.info("now callback");
            done.run(responsePrototype);
        }
    }


    /**
     * send protocol and get reply at there, you can do it by yourself, such as:
     * 1. sync: send data, then blocking wait until get peer server's reply
     * 2. async: send data, and set callback, then do other things. callback will run when get peer server's reply
     */
    protected abstract void ioHandler() throws TinyRpcSystemException;

}
