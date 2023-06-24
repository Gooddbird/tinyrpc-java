package com.iker.tinyrpc.net.rpc.protocol.tinypb;

import com.google.protobuf.*;
import com.iker.tinyrpc.net.rpc.RpcDispatcher;
import com.iker.tinyrpc.net.rpc.protobuf.ProtobufRpcServiceFactory;
import com.iker.tinyrpc.net.rpc.protobuf.TinyRpcController;
import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyRpcErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;



@Component
@Slf4j
public class TinyPBRpcDispatcher implements RpcDispatcher {

    @Override
    public void dispatch(RpcProtocol protocol, Channel channel) throws RuntimeException {
        TinyPBProtocol tinyPBProtocol = Optional.ofNullable((TinyPBProtocol) protocol).<RuntimeException>orElseThrow(
                () -> {
                    throw new TinyRpcSystemException("get null object of TinyPBProtocol");
                }
        );
        String msgReq = tinyPBProtocol.getMsgReq();

        TinyPBProtocol replyProtocol = new TinyPBProtocol();
        replyProtocol.setMsgReq(tinyPBProtocol.getMsgReq());
        replyProtocol.setServiceName(tinyPBProtocol.getServiceName());

        try {

            log.info(String.format("[%s] begin to dispatch rpc request", msgReq));

            String[] result = new String[2];
            parseMethodFullName(tinyPBProtocol.getServiceName(), result);
            String serviceName = result[0];
            String methodName = result[1];
            log.info(String.format("[%s] get serviceName [%s], get methodName [%s] from rpc request", msgReq, serviceName, methodName));

            // get RPC protobuf service object
            Service service = (Service) SpringContextUtil.getBean(ProtobufRpcServiceFactory.class).getService(serviceName).<TinyRpcSystemException>orElseThrow(
                    () -> {
                        throw new TinyRpcSystemException(TinyRpcErrorCode.ERROR_SERVICE_NOT_FOUND, String.format("[%s] not found service name of [%s]", msgReq, serviceName));
                    }
            );

            // find method by methodName
            Descriptors.MethodDescriptor methodDescriptor = Optional.ofNullable(service.getDescriptorForType().findMethodByName(methodName)).<TinyRpcSystemException>orElseThrow(
                    () -> {
                        throw new TinyRpcSystemException(TinyRpcErrorCode.ERROR_METHOD_NOT_FOUND, String.format("[%s] not found method name of [%s]", msgReq, methodName));
                    }
            );

            TinyRpcController rpcController = new TinyRpcController();
            rpcController.setMsgReq(tinyPBProtocol.getMsgReq());
            rpcController.setMethodFullName(tinyPBProtocol.getServiceName());
            rpcController.setMethodName(methodName);

            Message request = service.getRequestPrototype(methodDescriptor).newBuilderForType().mergeFrom(ByteString.copyFrom(tinyPBProtocol.getPbData(), StandardCharsets.ISO_8859_1)).build();
            service.callMethod(methodDescriptor, rpcController, request, parameter -> {

                String pbData = String.valueOf(Optional.ofNullable(parameter).<TinyRpcSystemException>orElseThrow(() -> {
                    throw new TinyRpcSystemException(String.format("[%s] msgReq execute method failed, get null response, controller error info[%s]", msgReq, rpcController.errorText()));
                }).toByteString());

                // rpc method run success, now reply to client
                replyProtocol.setPbData(pbData);
                replyResponse(replyProtocol, channel);
            });

        } catch (TinyRpcSystemException e) {
            // if occur some exception, you should reply to client, to tell error details
            log.error(String.format("[%s] dispatcher catch TinyRpcSystemException, error code[%d], error info[%s]", msgReq, e.getErrorCode().ordinal(), e.getErrorInfo()));
            log.error("Exception: ", e);
            replyProtocol.setErrCode(e.getErrorCode().ordinal());
            replyProtocol.setErrInfo(e.getErrorInfo());
            replyResponse(replyProtocol, channel);

        } catch (InvalidProtocolBufferException e) {
            log.error(String.format("[%s] dispatcher catch InvalidProtocolBufferException", msgReq));
            log.error("Exception: ", e);
        }

    }

    private void replyResponse(TinyPBProtocol replyProtocol, Channel channel) {
        channel.writeAndFlush(replyProtocol, channel.newPromise().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info(String.format("[%s] success write rpc response success of", replyProtocol.getMsgReq()));
            } else {
                log.error(String.format("[%s] failed write rpc response", replyProtocol.getMsgReq()));
                log.error("Exception " , future.cause());
            }
        }));
    }

    private void parseMethodFullName(String methodFullName, String[] result) {
        char split = '.';
        int i = methodFullName.indexOf(split);
        if (i == -1 || i != methodFullName.lastIndexOf(split)) {
            throw new TinyRpcSystemException(TinyRpcErrorCode.ERROR_PARSE_SERVICE_NAME,
                    String.format("parse serviceName error of [%s]", methodFullName));
        }
        result[0] = methodFullName.substring(0, i);
        result[1] = methodFullName.substring(i + 1);
    }
}
