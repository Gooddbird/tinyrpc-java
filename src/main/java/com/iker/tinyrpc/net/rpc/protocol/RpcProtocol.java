package com.iker.tinyrpc.net.rpc.protocol;


public interface RpcProtocol {

    void setMsgReq(String msgReq);

    String getMsgReq();

    int getMsgReqLen();

    Object getObject();

}
