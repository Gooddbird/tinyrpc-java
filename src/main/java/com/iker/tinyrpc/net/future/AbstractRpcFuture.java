package com.iker.tinyrpc.net.future;

import com.iker.tinyrpc.net.rpc.protocol.AbstractProtocol;
import lombok.Getter;

import java.util.concurrent.*;

public abstract class AbstractRpcFuture<T extends AbstractProtocol> implements Future<T> {

    protected T response;

    @Getter
    protected final String msgReq;

    public AbstractRpcFuture(String msgReq) {
        this.msgReq = msgReq;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    public abstract void setResponse(T response);
}
