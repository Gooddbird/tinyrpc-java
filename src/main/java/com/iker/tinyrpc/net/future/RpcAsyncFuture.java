package com.iker.tinyrpc.net.future;

import com.iker.tinyrpc.net.rpc.protocol.AbstractProtocol;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcAsyncFuture<T extends AbstractProtocol> extends AbstractRpcFuture<T> {

    public RpcAsyncFuture(String msgReq) {
        super(msgReq);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    @Override
    public void setResponse(T response) {
        this.response = response;
    }



}
