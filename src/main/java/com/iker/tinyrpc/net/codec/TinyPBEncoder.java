package com.iker.tinyrpc.net.codec;

import com.iker.tinyrpc.protocol.TinyPBProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
public class TinyPBEncoder extends MessageToByteEncoder {

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error occurs
     */
    @Override
    @SneakyThrows(IndexOutOfBoundsException.class)
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        log.debug("begin to do TinyPBEncoder.encode");
        TinyPBProtocol protocol = (TinyPBProtocol) msg;
        if (protocol == null) {
            log.error("object convert to TinyPBProtocol get null");
            return;
        }

        protocol.resetPackageLen();

        out.writeByte(TinyPBProtocol.getPbStart());         // set begin flag
        out.writeInt(protocol.getPkLen());
        out.writeInt(protocol.getMsgReqLen());
        out.writeBytes(protocol.getMsgReq().getBytes());
        out.writeInt(protocol.getServiceNameLen());
        out.writeBytes(protocol.getServiceName().getBytes());
        out.writeInt(protocol.getErrCode());
        out.writeInt(protocol.getErrInfoLen());
        out.writeBytes(protocol.getErrInfo().getBytes());
        out.writeBytes(protocol.getPbData().getBytes());
        out.writeInt(protocol.getCheckSum());
        out.writeByte(TinyPBProtocol.getPbEnd());       // set end flag

        log.debug("end TinyPBEncoder.encode");
    }
}
