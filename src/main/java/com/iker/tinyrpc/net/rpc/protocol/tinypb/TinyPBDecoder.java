package com.iker.tinyrpc.net.rpc.protocol.tinypb;

import com.iker.tinyrpc.util.TinyRpcErrorCode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class TinyPBDecoder extends ByteToMessageDecoder {

    /**
     * Decode the from one {@link ByteBuf} to an other. This method will be called till either the input
     * {@link ByteBuf} has nothing to read when return from this method or till nothing was read from the input
     * {@link ByteBuf}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @param out the {@link List} to which decoded messages should be added
     */
    @Override
    @SneakyThrows(IndexOutOfBoundsException.class)
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int from = in.readerIndex();
        log.info("begin to do TinyPBDecoder.decode");
        while (in.isReadable()) {
            int start = in.indexOf(from, in.writerIndex(), TinyPBProtocol.getPbStart());
            if (start == -1) {
                log.info("not find TinyPB protocol start PbStart(0x02), decode end");
                break;
            }
            log.debug(String.format("find start index %d", start));

            if (in.writerIndex() - start < TinyPBProtocol.getMinPkLen()) {
                log.error(String.format("writerIndex [%d], start [%d], read less min length of TinyPB package (%d)",
                        in.writerIndex(), start, TinyPBProtocol.getMinPkLen()));
                break;
            }
            int packageLenIndex = start + 1;
            int packageLen = in.getInt(packageLenIndex);
            log.info(String.format("get packageLen %d", packageLen));

            int end = start + packageLen - 1;
            if (end >= in.writerIndex()) {
                log.info(String.format("read less bytes than packageLen [%d]", packageLen));
                from = start + 1;
                continue;
            }

            if (in.getByte(end) != TinyPBProtocol.getPbEnd()) {
                log.info("read end index is not PbEnd(0x03)");
                from = start + 1;
                continue;
            }

            // get a full package, read it from ByteBuf
            in.readerIndex(end + 1);
            from = in.readerIndex();

            // from now, alloc an Object
            TinyPBProtocol request = new TinyPBProtocol();
            request.setPkLen(packageLen);

            if (packageLen < TinyPBProtocol.getMinPkLen() || packageLen > TinyPBProtocol.getMaxPkLen()) {
                // a bad package, directly drop it
                request.setErrCode(TinyRpcErrorCode.ERROR_FAILED_DECODE.ordinal());
                request.setErrInfo(String.format("read pkLen [%d] out of range", packageLen));
                out.add(request);
                continue;
            }

            // index of msgReqLen
            int msgReqLenIndex = packageLenIndex + 4;
            int msgReqLen = in.getInt(msgReqLenIndex);
            log.debug(String.format("read msgReqLen[%d]", msgReqLen));

            int msgReqIndex = msgReqLenIndex + 4;
            if (msgReqIndex + msgReqLen >= in.writerIndex()) {
                request.setErrCode(TinyRpcErrorCode.ERROR_FAILED_DECODE.ordinal());
                request.setErrInfo(String.format("read msgReqLen [%d] out of range", msgReqLen));
                out.add(request);
                continue;
            }
            request.setMsgReq(String.valueOf(in.getCharSequence(msgReqIndex, msgReqLen, CharsetUtil.UTF_8)));
            log.info(String.format("read msgReq [%s]", request.getMsgReq()));

            int serviceNameLenIndex = msgReqIndex + msgReqLen;
            int serviceNameLen = in.getInt(serviceNameLenIndex);
            log.debug(String.format("read serviceNameLen[%d]", serviceNameLen));

            int serviceNameIndex = serviceNameLenIndex + 4;
            if (serviceNameIndex + serviceNameLen >= in.writerIndex()) {
                request.setErrCode(TinyRpcErrorCode.ERROR_FAILED_DECODE.ordinal());
                request.setErrInfo(String.format("read serviceNameLen [%d] out of range", serviceNameLen));
                out.add(request);
                continue;
            }
            request.setServiceName(String.valueOf(in.getCharSequence(serviceNameIndex, serviceNameLen, CharsetUtil.UTF_8)));
            log.info(String.format("read serviceName [%s]", request.getServiceName()));

            int errCodeIndex = serviceNameIndex + serviceNameLen;
            request.setErrCode(in.getInt(errCodeIndex));

            int errInfoLenIndex = errCodeIndex + 4;
            int errInfoLen = in.getInt(errInfoLenIndex);

            int errInfoIndex = errInfoLenIndex + 4;
            request.setErrInfo(String.valueOf(in.getCharSequence(errInfoIndex, errInfoLen, CharsetUtil.UTF_8)));

            int pbDataIndex = errInfoIndex + errInfoLen;
            request.setPbData(String.valueOf(in.getCharSequence(pbDataIndex, end - pbDataIndex - 4, CharsetUtil.ISO_8859_1)));

            int checkSumIndex = end - 4;
            request.setCheckSum(in.getInt(checkSumIndex));
            out.add(request);
            log.info(String.format("success decode a TinyPBProtocol of msgReq[%s]", request.getMsgReq()));
        }
        log.info("end TinyPBDecoder.decode");
    }
}
