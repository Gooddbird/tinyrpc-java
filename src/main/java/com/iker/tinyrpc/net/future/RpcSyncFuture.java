package com.iker.tinyrpc.net.future;

import com.iker.tinyrpc.net.rpc.protocol.AbstractProtocol;

import java.util.concurrent.*;

public class RpcSyncFuture<T extends AbstractProtocol> extends AbstractRpcFuture<T> {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public RpcSyncFuture(String msgReq) {
        super(msgReq);
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

    @Override
    public T get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (countDownLatch.await(timeout, unit)) {
            return response;
        }
        return null;
    }


    @Override
    public void setResponse(T response) {
        this.response = response;
        countDownLatch.countDown();
    }



}
