package com.iker.tinyrpc.net.rpc;

import com.iker.tinyrpc.util.TinyRpcSystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class RpcServiceFactory {

    @Resource
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
