package com.iker.tinyrpc.net.rpc.protocol;

import lombok.Getter;
import lombok.Setter;

public class AbstractProtocol {

    @Getter
    protected String msgReq;        // uuid to identify request or response

    @Getter
    private int msgReqLen;          // length of msgReq

    public void setMsgReq(String msgReq) {
        this.msgReq = msgReq;
        msgReqLen = msgReq.length();
    }
}
