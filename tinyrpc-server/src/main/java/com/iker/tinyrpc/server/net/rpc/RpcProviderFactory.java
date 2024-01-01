package com.iker.tinyrpc.server.net.rpc;

import com.iker.tinyrpc.server.utils.exception.TinyRpcSystemException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class RpcProviderFactory {

    private Map<String, Object> rpcServiceMap;

    public void registerService(String key, Object object) {
        if (rpcServiceMap.containsKey(key)) {
            throw new TinyRpcSystemException(String.format("registerService error, key %s exist", key));
        }
        rpcServiceMap.put(key, object);
        log.info(String.format("register %s success", key));
    }

    public Optional<Object> getService(String key) {
        return Optional.ofNullable(rpcServiceMap.get(key));
    }


}
