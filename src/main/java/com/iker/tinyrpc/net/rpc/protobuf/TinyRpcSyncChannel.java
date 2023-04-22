package com.iker.tinyrpc.net.rpc.protobuf;

import com.iker.tinyrpc.net.rpc.RpcFutureFactory;
import com.iker.tinyrpc.net.rpc.protocol.AbstractProtocol;
import com.iker.tinyrpc.net.future.AbstractRpcFuture;
import com.iker.tinyrpc.net.future.RpcSyncFuture;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBProtocol;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyPBErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class TinyRpcSyncChannel extends AbstractProtobufRpcChannel {

    public TinyRpcSyncChannel(InetSocketAddress peerAddr) {
        super(peerAddr);
    }

    /**
     * send protocol and get reply at there, you can do it by yourself, such as:
     * 1. sync: send data, then blocking wait until get peer server's reply
     * 2. async: send data, and set callback, then do other things. callback will run when get peer server's reply
     */
    @Override
    protected void ioHandler() throws TinyRpcSystemException {

        asyncConnect();

        RpcSyncFuture<TinyPBProtocol> syncFuture = new RpcSyncFuture<>(sendProtocol.getMsgReq());
        SpringContextUtil.getBean("tinyrpc-rpcFutureFactory", RpcFutureFactory.class).addFuture(syncFuture);

        tcpClient.sendMessage(sendProtocol);

        try {
            replyProtocol = syncFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new TinyRpcSystemException(TinyPBErrorCode.ERROR_UNKNOWN, String.format("unknown error: %s", e.getMessage()));
        }


    }

}
