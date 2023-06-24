package com.iker.tinyrpc.net.rpc;

import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import io.netty.channel.Channel;

public interface RpcDispatcher {
    /**
     * @param protocol protocol object, such as TinyPBProtocol
     * @throws TinyRpcSystemException
     * To dispatch rpc request according protocol, so that can server call designated method do business things, and reply to client
     */
    void dispatch(RpcProtocol protocol, Channel channel) throws TinyRpcSystemException;
}
