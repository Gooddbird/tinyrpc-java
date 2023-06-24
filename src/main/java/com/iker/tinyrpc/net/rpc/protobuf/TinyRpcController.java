package com.iker.tinyrpc.net.rpc.protobuf;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.iker.tinyrpc.util.TinyRpcErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public class TinyRpcController implements RpcController {

    /**
     * error_code, identify one specific error
     */
    @Getter
    @Setter
    private TinyRpcErrorCode errCode;

    /**
     * error_info, details description of error
     */
    @Getter
    @Setter
    private String errInfo = "";

    /**
     * msg_req, identify once rpc request and response
     */
    @Getter
    @Setter
    private String msgReq = "";

    /**
     * methodName of rpc call, such as queryName
     */
    @Getter
    @Setter
    private String methodName = "";

    /**
     * methodFullName of rpc call, such as QueryService.queryName
     */
    @Getter
    @Setter
    private String methodFullName = "";

    /**
     * localAddr of rpc call
     */
    @Getter
    @Setter
    private InetSocketAddress localAddr;

    /**
     * peerAddr of rpc call
     */
    @Getter
    @Setter
    private InetSocketAddress peerAddr;


    /**
     * show rpc progress is failed
     */
    private boolean isFailed;

    /**
     * show is cancel rpc progress
     */
    @Setter
    private boolean isCanceled;

    @Override
    public void reset() {
        isCanceled = false;
        isFailed = false;
        peerAddr = null;
        localAddr = null;
        methodName = null;
        methodFullName = null;
        msgReq = null;
        errCode = null;
        errInfo = null;
    }

    @Override
    public boolean failed() {
        return isFailed;
    }

    @Override
    public String errorText() {
        return errInfo;
    }

    @Override
    public void startCancel() {

    }

    @Override
    public void setFailed(String reason) {
        errInfo = reason;
        isFailed = true;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void notifyOnCancel(RpcCallback<Object> callback) {

    }
}
