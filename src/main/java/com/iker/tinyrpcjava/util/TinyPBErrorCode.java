package com.iker.tinyrpcjava.util;

public enum TinyPBErrorCode {
    ERROR_FAILED_CONNECT(10000002),        // failed to connection peer host
    ERROR_FAILED_GET_REPLY(10000002),      // failed to get server reply
    ERROR_FAILED_DESERIALIZE(10000003),    // deserialize failed
    ERROR_FAILED_SERIALIZE(10000004),      // serialize failed
    ERROR_FAILED_ENCODE(10000005),      // encode failed
    ERROR_FAILED_DECODE(10000006),     // decode failed
    ERROR_RPC_CALL_TIMEOUT(10000007),    // call rpc timeout
    ERROR_SERVICE_NOT_FOUND(10000008),    // not found service name
    ERROR_METHOD_NOT_FOUND(10000009),    // not found method
    ERROR_PARSE_SERVICE_NAME(10000010),    // not found service name
    ERROR_NOT_SET_ASYNC_PRE_CALL(10000011),            // you didn't set some necessary param before call async rpc
    ERROR_CONNECT_SYS_ERR(10000012);           // connect sys error

    TinyPBErrorCode(int i) {

    }
}
