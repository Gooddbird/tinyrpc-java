package com.iker.tinyrpc.util;

public class TinyRpcSystemException extends RuntimeException {

    public TinyRpcSystemException(TinyPBErrorCode errorFailedDecode) {
        super("Error code: " + errorFailedDecode.name());
    }

    public TinyRpcSystemException(TinyPBErrorCode errorFailedDecode, String s) {
        super("Error code: " + errorFailedDecode.name() + ", Error info: " + s);
    }

    public TinyRpcSystemException(String s) {
        super("Error info: " + s);
    }
}
