package com.iker.tinyrpc.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TinyRpcSystemException extends RuntimeException {

    @Getter
    @Setter
    private TinyPBErrorCode errorCode;

    @Getter
    @Setter
    private String errorInfo;


    public TinyRpcSystemException(TinyPBErrorCode errorFailedDecode, String s) {
        super("Error code: " + errorFailedDecode.name() + ", Error info: " + s);
        setErrorCode(errorFailedDecode);
        setErrorInfo(s);
        log.error("Error code: " + errorFailedDecode.name() + ", Error info: " + s);
    }

    public TinyRpcSystemException(String s) {
        super("Error info: " + s);
        setErrorInfo(s);
    }
}
