package com.iker.tinyrpcjava.protocol;

import lombok.Getter;
import lombok.Setter;

public class TinyPBProtocol extends AbstractProtocol {
    @Getter
    @Setter
    private int pkLen;      // length of all package

    @Getter
    @Setter
    private int msgReqLen;  // length of msgReq

    @Getter
    @Setter
    private String msgReq;  // identify a request or response

    @Getter
    @Setter
    private int serviceNameLen; // length of service name

    @Getter
    @Setter
    private String serviceName; // service full name, like QueryService.query_name

    @Getter
    @Setter
    private int errCode;    // system error code,  0 -- call rpc success, otherwise -- call rpc failed. it only set in RpcController

    @Getter
    @Setter
    private int errInfoLen;  // length of error info

    @Getter
    @Setter
    private String errInfo;  // error detail info of Rpc, empty -- when call rpc success

    @Getter
    @Setter
    private String pbData;  // protobuf message object serialized bytes

    @Getter
    @Setter
    private int checkSum;
}
