package com.iker.tinyrpc.net.rpc;

import com.google.protobuf.*;
import com.iker.tinyrpc.protocol.AbstractProtocol;
import com.iker.tinyrpc.protocol.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyRpcSystemException;

import com.iker.tinyrpc.util.TinyPBErrorCode;

import java.util.Optional;


public class TinyPBRpcDispatcher extends AbstractRpcDispatcher {

    @Override
    void dispatch(AbstractProtocol protocol) throws TinyRpcSystemException {
        TinyPBProtocol tinyPBProtocol = (TinyPBProtocol) protocol;
        if (tinyPBProtocol == null) {
            throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_FAILED_DESERIALIZE, "failed to deserialize TinyPB protocol object, get null object");
        }
        String serviceName = "";
        String methodName = "";

        // get RPC protobuf service object
        Service service = (Service) SpringContextUtil.getBean("rpcServiceFactory", RpcServiceFactory.class).getService(serviceName).orElseThrow(
                () -> {
                    throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_SERVICE_NOT_FOUND, String.format("not found service name of [%s]", serviceName));
                }
        );

        // find method by methodName
        Descriptors.MethodDescriptor methodDescriptor = Optional.ofNullable(service.getDescriptorForType().findMethodByName(methodName)).orElseThrow(
                () -> {
                    throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_METHOD_NOT_FOUND, String.format("not found service name of [%s]", serviceName));
                }
        );

        try {
            Message request = service.getRequestPrototype(methodDescriptor).newBuilderForType().mergeFrom(ByteString.copyFromUtf8(tinyPBProtocol.getPbData())).build();
            TinyPBRpcController rpcController = new TinyPBRpcController();
            rpcController.setMsgReq(tinyPBProtocol.getMsgReq());
            rpcController.setMethodFullName(tinyPBProtocol.getServiceName());
            rpcController.setMethodName(methodName);

//            service.callMethod(methodDescriptor, rpcController, request, );

        } catch (InvalidProtocolBufferException e) {
            throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_FAILED_DESERIALIZE, "failed deserialize protobuf data from request");
        }



    }
}
