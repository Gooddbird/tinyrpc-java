package com.iker.tinyrpc.util;

public enum TinyPBErrorCode {
    /**
     * unknown error
     */
    ERROR_UNKNOWN(10000000),

    /**
     * failed to connection peer host
     */
    ERROR_FAILED_CONNECT(10000001),

    /**
     * failed to get server reply
     */
    ERROR_FAILED_GET_REPLY(10000002),
    /**
     * deserialize protobuf data failed
     */
    ERROR_FAILED_DESERIALIZE(10000003),
    /**
     * serialize protobuf data failed
     */
    ERROR_FAILED_SERIALIZE(10000004),
    /**
     * encode failed
     */
    ERROR_FAILED_ENCODE(10000005),
    /**
     * decode failed
     */
    ERROR_FAILED_DECODE(10000006),
    /**
     * call rpc timeout
     */
    ERROR_RPC_CALL_TIMEOUT(10000007),
    /**
     * not found service name
     */
    ERROR_SERVICE_NOT_FOUND(10000008),
    /**
     * not found method name
     */
    ERROR_METHOD_NOT_FOUND(10000009),
    /**
     * parse service full name error
     */
    ERROR_PARSE_SERVICE_NAME(10000010),
    /**
     * didn't set some necessary param before call async rpc, it only happened on tinyrpc-cpp
     */
    ERROR_NOT_SET_ASYNC_PRE_CALL(10000011),
    /**
     * connect peer addr sys error
     */
    ERROR_CONNECT_SYS_ERR(10000012),

    /**
     * execute rpc method failed, get null response
     */
    ERROR_EXECUTE_RPC_METHOD(10000013);

    TinyPBErrorCode(int i) {

    }
}
