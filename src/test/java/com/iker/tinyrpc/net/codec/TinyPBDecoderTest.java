package com.iker.tinyrpc.net.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBDecoder;
import com.iker.tinyrpc.proto.queryAgeReq;
import com.iker.tinyrpc.proto.queryNameReq;
import com.iker.tinyrpc.net.rpc.protocol.tinypb.TinyPBProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static io.netty.buffer.Unpooled.buffer;

@Slf4j
class TinyPBDecoderTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    public ByteBuf getByteBuf(TinyPBProtocol protocol) {
        ByteBuf byteBuf = buffer(256);
        byteBuf.writeInt(9891);

        byteBuf.writeByte(TinyPBProtocol.getPbStart());         // set begin flag

        byteBuf.writeInt(protocol.getPkLen());
        byteBuf.writeInt(protocol.getMsgReqLen());
        byteBuf.writeBytes(protocol.getMsgReq().getBytes());
        byteBuf.writeInt(protocol.getServiceNameLen());
        byteBuf.writeBytes(protocol.getServiceName().getBytes());
        byteBuf.writeInt(protocol.getErrCode());
        byteBuf.writeInt(protocol.getErrInfoLen());
        byteBuf.writeBytes(protocol.getErrInfo().getBytes());
        byteBuf.writeBytes(protocol.getPbData().getBytes(CharsetUtil.ISO_8859_1));
        byteBuf.writeInt(0);

        byteBuf.writeByte(TinyPBProtocol.getPbEnd());       // set end flag

        log.debug(String.format("get read index %d, get write index %d", byteBuf.readerIndex(), byteBuf.writerIndex()));

        return byteBuf;
    }



    @Test
    void decode() throws InvalidProtocolBufferException, UnsupportedEncodingException {
        queryNameReq request = queryNameReq.newBuilder().setReqNo(999).setId(1).build();

        TinyPBProtocol protocol = new TinyPBProtocol();
        try {
            protocol.setPbData(request.toByteString().toString("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        };
        String msgReq = "1234567890";
        protocol.setMsgReq(msgReq);
        String serviceName = "QueryService.query_name";
        protocol.setServiceName(serviceName);
        protocol.resetPackageLen();
        log.info(String.format("package length is %d", protocol.getPkLen()));

        ByteBuf byteBuf = getByteBuf(protocol);
        List<Object> out = new ArrayList<>();

        TinyPBDecoder decoder = new TinyPBDecoder();
        decoder.decode(null, byteBuf, out);
        log.info("get out list size " + out.size());

        for (Object a: out) {
            TinyPBProtocol tinyPBProtocol = (TinyPBProtocol)a;
            if (tinyPBProtocol != null) {
                log.info(String.format("get TinyPBProtocol, packageLen[%d], msgReq[%s], serviceName[%s], errCode[%d], errInfo[%s],, checkSum[%d]",
                        tinyPBProtocol.getPkLen(), tinyPBProtocol.getMsgReq(), tinyPBProtocol.getServiceName(), tinyPBProtocol.getErrCode(),
                        tinyPBProtocol.getErrInfo(), tinyPBProtocol.getCheckSum()));
                try {
                    queryAgeReq.parseFrom(protocol.getPbData().getBytes());
                    queryAgeReq.parseFrom(tinyPBProtocol.getPbData().getBytes());
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }
}