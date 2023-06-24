package com.iker.tinyrpc.net.rpc.protocol.tinypb;

import com.google.protobuf.Message;
import com.iker.tinyrpc.net.rpc.protocol.RpcProtocol;
import lombok.Getter;
import lombok.Setter;

public class TinyPBProtocol implements RpcProtocol {

    @Getter
    private static final byte pbStart = 0x02;

    @Getter
    private static final byte pbEnd = 0x03;

    @Getter
    private static final int minPkLen = 26;         // min length of a TinyPB protocol package. include pbStart and pbEnd

    @Getter
    private static final int maxPkLen = 65536;         // max length of a TinyPB protocol package.

    @Setter
    private int pkLen;      // length of all package

    private String msgReq = ""; // length of service name

    @Getter
    private int serviceNameLen; // length of service name

    @Getter
    private String serviceName = ""; // service full name, like QueryService.query_name


    @Getter
    @Setter
    private int errCode;    // system error code,  0 -- call rpc success, otherwise -- call rpc failed. it only set in RpcController

    @Getter
    private int errInfoLen;  // length of error info

    @Getter
    private String errInfo = "";  // error detail info of Rpc, empty -- when call rpc success

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
        errInfoLen = errInfo.length();
    }

    @Getter
    @Setter
    private String pbData = "";  // protobuf message object serialized bytes

    @Getter
    @Setter
    private int checkSum;

    public int getPkLen() {
        resetPackageLen();
        return pkLen;
    }


    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
        serviceNameLen = serviceName.length();
    }

    public void resetPackageLen() {
        pkLen = msgReq.length() + serviceName.length() + errInfo.length() + pbData.length() + TinyPBProtocol.getMinPkLen();
    }

    @Override
    public void setMsgReq(String msgReq) {
        this.msgReq = msgReq;
    }

    @Override
    public String getMsgReq() {
        return msgReq;
    }

    @Override
    public int getMsgReqLen() {
        return msgReq.length();
    }

    @Override
    public Message getObject() {
        return null;
    }
}
