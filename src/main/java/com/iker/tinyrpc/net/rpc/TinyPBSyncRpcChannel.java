package com.iker.tinyrpc.net.rpc;

import java.net.InetSocketAddress;

public class TinyPBSyncRpcChannel  extends TinyPBRpcChannel {

    protected TinyPBSyncRpcChannel(InetSocketAddress peerAddr) {
        super(peerAddr);
    }

    /**
     * send protocol and get reply at there, you can do it by yourself, such as:
     * 1. sync: send data, then blocking wait until get peer server's reply
     * 2. async: send data, and set callback, then do other things. callback will run when get peer server's reply
     */
    @Override
    protected void ioHandler() throws InterruptedException {

    }

}
