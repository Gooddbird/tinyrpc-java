package com.iker.tinyrpc.net.rpc;

import com.iker.tinyrpc.net.future.RpcFuture;
import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RpcFutureMap {

    private final Map<String, RpcFuture<RpcProtocol>> rpcResponseMap = new HashMap<>();

    public void addFuture(RpcFuture<RpcProtocol> future) {
        synchronized (rpcResponseMap) {
            rpcResponseMap.put(future.getId(), future);
        }

    }

    public RpcFuture<RpcProtocol> getFuture(String key) {
        synchronized (rpcResponseMap) {
            if (rpcResponseMap.containsKey(key)) {
                return rpcResponseMap.get(key);
            }
            return null;
        }
    }

    public boolean deleteFuture(String key) {
        synchronized (rpcResponseMap) {
            if (rpcResponseMap.containsKey(key)) {
                rpcResponseMap.remove(key);
                return true;
            }
            return false;
        }
    }
}
