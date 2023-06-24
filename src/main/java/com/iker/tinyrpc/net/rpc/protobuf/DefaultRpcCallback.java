package com.iker.tinyrpc.net.rpc.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;

public interface DefaultRpcCallback {
    void run();
}
