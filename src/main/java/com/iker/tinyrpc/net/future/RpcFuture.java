package com.iker.tinyrpc.net.future;

import java.util.concurrent.*;

public interface RpcFuture<T> extends Future<T> {
    String getId();

    // to invoke the future who is call get()
    void invoke(T object);
}
