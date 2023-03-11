package com.iker.tinyrpc.codec;

import com.iker.tinyrpc.net.codec.TinyPBDecoder;
import com.iker.tinyrpc.protocol.TinyPBProtocol;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        byteBuf.writeBytes(protocol.getPbData().getBytes());
        byteBuf.writeInt(0);

        byteBuf.writeByte(TinyPBProtocol.getPbEnd());       // set end flag

        log.debug(String.format("get read index %d, get write index %d", byteBuf.readerIndex(), byteBuf.writerIndex()));

        return byteBuf;
    }



    @Test
    void decode() {
        TinyPBDecoder decoder = new TinyPBDecoder();

        TinyPBProtocol protocol = new TinyPBProtocol();
        protocol.setPbData("test");
        String msgReq = "1234567890";
        protocol.setMsgReq(msgReq);
        protocol.setErrCode(0);
        protocol.setErrInfo("");
        String serviceName = "TestService.query";
        protocol.setServiceName(serviceName);
        protocol.resetPackageLen();

        ByteBuf byteBuf = getByteBuf(protocol);
        List<Object> out = new ArrayList<>();
        decoder.decode(null, byteBuf, out);
        log.debug("get out list size " + out.size());

        for (Object a: out) {
            TinyPBProtocol tinyPBProtocol = (TinyPBProtocol)a;
            if (tinyPBProtocol != null) {
                log.debug(String.format("get TinyPBProtocol, packageLen[%d], msgReq[%s], serviceName[%s], errCode[%d], errInfo[%s], pbData[%s], checkSum[%d]",
                        tinyPBProtocol.getPkLen(), tinyPBProtocol.getMsgReq(), tinyPBProtocol.getServiceName(), tinyPBProtocol.getErrCode(),
                        tinyPBProtocol.getErrInfo(), tinyPBProtocol.getPbData(), tinyPBProtocol.getCheckSum()));
            }
        }


    }
}