package com.iker.tinyrpc.net.rpc.protobuf;

import com.google.protobuf.*;
import com.iker.tinyrpc.net.TcpClient;
import com.iker.tinyrpc.net.TcpServer;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyPBErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public abstract class AbstractProtobufRpcChannel implements RpcChannel {
    protected final InetSocketAddress peerAddr;

    protected TinyPBProtocol sendProtocol;

    protected TinyPBProtocol replyProtocol;

    protected TinyRpcController tinyRpcController;

    protected TcpClient tcpClient;

    public AbstractProtobufRpcChannel(InetSocketAddress peerAddr) {
        this.peerAddr = peerAddr;
    }

    @Override
    public void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, Message responsePrototype, RpcCallback<Message> done) {
        try {

            assert (method != null);

            tinyRpcController = (TinyRpcController) controller;

            fillRpcController(method);

            generateTinyPBProtocol(request);

            // do async connect
            ioHandler();

            fillResponse(responsePrototype);

        } catch (TinyRpcSystemException e) {
            log.error(String.format("call rpc occur TinyRpcSystemException, error code [%d], error info [%s]", e.getErrorCode().ordinal(), e.getErrorInfo()));
            tinyRpcController.setErrCode(e.getErrorCode());
            tinyRpcController.setFailed(e.getErrorInfo());
        } catch (InvalidProtocolBufferException e) {
            log.error(String.format("call rpc occur InvalidProtocolBufferException, error info [%s]", e.getMessage()));
            tinyRpcController.setErrCode(TinyPBErrorCode.ERROR_FAILED_DESERIALIZE);
            tinyRpcController.setFailed("failed to deserialize response protobuf data");
        }
//        } catch (RuntimeException e) {
//            log.error(String.format("call rpc occur Unknown RuntimeException, error info [%s]", e.getMessage()));
//            tinyRpcController.setErrCode(TinyPBErrorCode.ERROR_UNKNOWN);
//            tinyRpcController.setFailed(String.format("unknown exception, error info: %s", e.getMessage()));
//        }
    }

    protected void fillRpcController(Descriptors.MethodDescriptor method) {

        tinyRpcController.setMethodFullName(method.getFullName());
        tinyRpcController.setMethodName(method.getName());
        tinyRpcController.setPeerAddr(peerAddr);

        if (tinyRpcController.getMsgReq().isEmpty()) {
            tinyRpcController.setMsgReq("123456789");
        }

    }

    protected void fillResponse(Message responsePrototype) throws InvalidProtocolBufferException {
        if (replyProtocol != null) {
            responsePrototype.toBuilder().mergeFrom(ByteString.copyFrom(replyProtocol.getPbData(), StandardCharsets.ISO_8859_1)).build();
        }
    }


    protected void generateTinyPBProtocol(Message request){

        sendProtocol = new TinyPBProtocol();

        sendProtocol.setMsgReq(tinyRpcController.getMsgReq());
        try {
            sendProtocol.setPbData(request.toByteString().toString("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_FAILED_DESERIALIZE, "failed to deserialize request data");
        }
        sendProtocol.setServiceName(tinyRpcController.getMethodFullName());
        sendProtocol.resetPackageLen();

    }

    protected void asyncConnect() {
        tcpClient = new TcpClient(peerAddr, Optional.ofNullable(SpringContextUtil.getBean("tinyrpc-TcpServer", TcpServer.class).getWorkerLoopGroup()).orElse(
                new NioEventLoopGroup(1)
        ).next());

        tcpClient.connect();
    }


    /**
     * send protocol and get reply at there, you can do it by yourself, such as:
     * 1. sync: send data, then blocking wait until get peer server's reply
     * 2. async: send data, and set callback, then do other things. callback will run when get peer server's reply
     */
    protected abstract void ioHandler() throws TinyRpcSystemException;

}
