package com.iker.tinyrpc.net;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.iker.tinyrpc.annotation.TinyPBService;
import com.iker.tinyrpc.proto.queryAgeReq;
import com.iker.tinyrpc.proto.queryAgeRes;
import com.iker.tinyrpc.proto.queryNameReq;
import com.iker.tinyrpc.proto.queryNameRes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@TinyPBService
public class QueryServiceHandler extends com.iker.tinyrpc.proto.QueryService {

    @Override
    public void queryName(RpcController controller, queryNameReq request, RpcCallback<queryNameRes> done) {
        queryNameRes response = queryNameRes.newBuilder().setName("tinyrpc-test-queryNameMethod").setId(8888).build();
        log.info("call method queryName successful");
        done.run(response);
    }

    @Override
    public void queryAge(RpcController controller, queryAgeReq request, RpcCallback<queryAgeRes> done) {
        queryAgeRes response = queryAgeRes.newBuilder().setAge(9999).setId(8888).build();
        log.info("call method queryAge successful");
        done.run(response);
    }
};