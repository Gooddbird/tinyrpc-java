package com.iker.tinyrpc.net.rpc;

import com.google.protobuf.*;
import com.iker.tinyrpc.protocol.AbstractProtocol;
import com.iker.tinyrpc.protocol.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyRpcSystemException;

import com.iker.tinyrpc.util.TinyPBErrorCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;



@Component
@Slf4j
public class TinyPBRpcDispatcher extends AbstractRpcDispatcher {

    @Override
    public void dispatch(AbstractProtocol protocol, Channel channel) throws RuntimeException {
        TinyPBProtocol tinyPBProtocol = Optional.ofNullable((TinyPBProtocol) protocol).<RuntimeException>orElseThrow(
                () -> {
                    throw new RuntimeException("get null object of TinyPBProtocol");
                }
        );

        try {
            String msgReq = tinyPBProtocol.getMsgReq();
            log.info(String.format("begin to dispatch rpc request of msgReq [%s]", msgReq));

            String[] result = new String[2];
            parseMethodFullName(tinyPBProtocol.getServiceName(), result);
            String serviceName = result[0];
            String methodName = result[1];
            log.info(String.format("get serviceName [%s], get methodName [%s] from rpc request of msgReq[%s]", serviceName, methodName, msgReq));

            // get RPC protobuf service object
            Service service = (Service) SpringContextUtil.getBean("rpcServiceFactory", RpcServiceFactory.class).getService(serviceName).<TinyRpcSystemException>orElseThrow(
                    () -> {
                        throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_SERVICE_NOT_FOUND, String.format("rpc request of msgReq [%s] not found service name of [%s]", msgReq, serviceName));
                    }
            );

            // find method by methodName
            Descriptors.MethodDescriptor methodDescriptor = Optional.ofNullable(service.getDescriptorForType().findMethodByName(methodName)).<TinyRpcSystemException>orElseThrow(
                    () -> {
                        throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_METHOD_NOT_FOUND, String.format("rpc request of msgReq [%s] not found service name of [%s]", msgReq, methodName));
                    }
            );

            TinyPBRpcController rpcController = new TinyPBRpcController();
            rpcController.setMsgReq(tinyPBProtocol.getMsgReq());
            rpcController.setMethodFullName(tinyPBProtocol.getServiceName());
            rpcController.setMethodName(methodName);

            Message request = service.getRequestPrototype(methodDescriptor).newBuilderForType().mergeFrom(ByteString.copyFrom(tinyPBProtocol.getPbData(), StandardCharsets.ISO_8859_1)).build();
            service.callMethod(methodDescriptor, rpcController, request, new RpcCallback<Message>() {
                @Override
                public void run(Message parameter) {

                    String pbData = String.valueOf(Optional.ofNullable(parameter).<TinyRpcSystemException>orElseThrow(() -> {
                        throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_EXECUTE_RPC_METHOD,
                                String.format("msgReq [%s] execute method failed, get null response, controller error info[%s]", msgReq, rpcController.errorText()));
                    }).toByteString());

                    // rpc method run success, now reply to client
                    TinyPBProtocol replyProtocol = new TinyPBProtocol();
                    replyProtocol.setMsgReq(msgReq);
                    replyProtocol.setServiceName(tinyPBProtocol.getServiceName());
                    replyProtocol.setPbData(pbData);
                    replyResponse(replyProtocol, channel);
                }
            });

        } catch (TinyRpcSystemException e) {
            // if occur some exception, you should reply to client, to tell error details
            log.error(String.format("dispatcher catch TinyRpcSystemException, error code[%d], error info[%s]", e.getErrorCode().ordinal(), e.getErrorInfo()));
            TinyPBProtocol replyProtocol = new TinyPBProtocol();
            replyProtocol.setMsgReq(tinyPBProtocol.getMsgReq());
            replyProtocol.setErrCode(e.getErrorCode().ordinal());
            replyProtocol.setErrInfo(e.getErrorInfo());
            replyProtocol.setServiceName(tinyPBProtocol.getServiceName());

            replyResponse(replyProtocol, channel);

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

    }

    private void replyResponse(TinyPBProtocol replyProtocol, Channel channel) {
        channel.writeAndFlush(replyProtocol, channel.newPromise().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info(String.format("success write rpc response success of msgReq[%s]", replyProtocol.getMsgReq()));
            } else {
                log.error(String.format("failed write rpc response of msgReq[%s]", replyProtocol.getMsgReq()));
                future.cause().printStackTrace();
                throw new RuntimeException(future.cause().getMessage());
            }
        }));
    }

    private void parseMethodFullName(String methodFullName, String[] result) {
        char split = '.';
        int i = methodFullName.indexOf(split);
        if (i == -1 || i != methodFullName.lastIndexOf(split)) {
            throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_PARSE_SERVICE_NAME,
                    String.format("parse serviceName error of [%s]", methodFullName));
        }
        result[0] = methodFullName.substring(0, i);
        result[1] = methodFullName.substring(i + 1);
    }
}
