package com.iker.tinyrpc.net.future;

import com.iker.tinyrpc.net.rpc.protobuf.DefaultRpcCallback;
import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RpcSyncFuture implements RpcFuture<RpcProtocol> {

    private static final int DEFAULT_TIMEOUT = 2000;        // 默认超时时间，2000 ms

    private final String id;

    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private final DefaultRpcCallback callback;

    @Setter
    private boolean done = false;

    @Setter
    private boolean canceled = false;

    @Setter
    private boolean needInvoke = false;

    @Setter
    private RpcProtocol reply;

    public RpcSyncFuture(String id, DefaultRpcCallback callback) {
        this.id = id;
        this.callback = callback;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (canceled) {
            return true;
        }
        canceled = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcProtocol get() throws InterruptedException, ExecutionException {
        return get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }


    public RpcProtocol get(long timeout) throws InterruptedException, ExecutionException {
        return get(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public RpcProtocol get(long timeout, TimeUnit unit) {
        if (done) {
            return reply;
        }

        lock.lock();
        needInvoke = true;
        if (reply != null) {
            return reply;
        }
        try {
            while (!done) {
                if(condition.await(timeout, unit)){
                    // invoked by other thread
                    log.info("RpcFuture:{} invoked", id);
                } else {
                    // timeout
                    log.info("RpcFuture:{} timeout", id);
                }
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
        } finally {
            lock.unlock();
        }

        runCallback();

        return reply;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void invoke(RpcProtocol object) {
        if (done) {
            return;
        }
        // 是否需要 notify 原线程
        // 如果主调线程调用了 get, 就需要 notify
        // 否则直接当前线程执行回调即可
        if (isNeedInvoke()){
            lock.lock();

            reply = object;

            setDone(true);

            condition.signal();

            lock.unlock();

        } else {
            runCallback();
        }
    }

    private boolean isNeedInvoke () {
        return needInvoke;
    }

    private void runCallback() {
        Optional.ofNullable(callback).ifPresent(
                DefaultRpcCallback::run
        );
    }
}
