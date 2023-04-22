package com.iker.tinyrpc.net.rpc;

import com.iker.tinyrpc.net.rpc.protocol.AbstractProtocol;
import io.netty.channel.Channel;

public interface RpcDispatcher {
    /**
     * @param protocol protocol object, such as TinyPBProtocol
     * @throws RuntimeException
     * To dispatch rpc request according protocol, so that can server call designated method do business things, and reply to client
     */
    abstract public void dispatch(AbstractProtocol protocol, Channel channel) throws RuntimeException;
}
