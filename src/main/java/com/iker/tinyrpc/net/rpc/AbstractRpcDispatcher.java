package com.iker.tinyrpc.net.rpc;

import com.iker.tinyrpc.protocol.AbstractProtocol;
import com.iker.tinyrpc.util.TinyRpcSystemException;

public abstract class AbstractRpcDispatcher {
    /**
     * @param protocol protocol object, such as TinyPBProtocol
     * @throws TinyRpcSystemException
     * To dispatch rpc request according protocol, so that can server call designated method do business things, and reply to client
     */
    abstract void dispatch(AbstractProtocol protocol) throws TinyRpcSystemException;
}
