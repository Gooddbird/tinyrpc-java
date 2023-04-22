package com.iker.tinyrpc.net.rpc;

import com.iker.tinyrpc.net.future.AbstractRpcFuture;
import com.iker.tinyrpc.net.rpc.protocol.AbstractProtocol;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component(value = "tinyrpc-rpcFutureFactory")
public class RpcFutureFactory {

    private final Map<String, AbstractRpcFuture<? extends AbstractProtocol>> rpcResponseMap = new HashMap<>();

    public void addFuture(AbstractRpcFuture<? extends AbstractProtocol> future) {
        synchronized (rpcResponseMap) {
            rpcResponseMap.put(future.getMsgReq(), future);
        }

    }

    public AbstractRpcFuture getFuture(String key) {
        synchronized (rpcResponseMap) {
            if (rpcResponseMap.containsKey(key)) {
                return rpcResponseMap.get(key);
            }
            return null;
        }
    }
}
