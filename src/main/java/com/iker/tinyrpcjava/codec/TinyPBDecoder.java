package com.iker.tinyrpcjava.codec;

import com.iker.tinyrpcjava.protocol.TinyPBProtocol;
import com.iker.tinyrpcjava.util.TinyPBErrorCode;
import com.iker.tinyrpcjava.util.TinyRpcSystemException;
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int from = in.readerIndex();
        while (in.isReadable()) {
            int start = in.indexOf(from, in.writerIndex(), TinyPBProtocol.getPbStart());
            if (start == -1) {
                log.debug("not find TinyPB protocol start PbStart(0x02), decode end");
                break;
            }

            if (in.writerIndex() - start < TinyPBProtocol.getMinPkLen()) {
                log.debug("read less min length of TinyPB package (26)");
                break;
            }
            int packageLenIndex = start + 1;
            int packageLen = in.getInt(packageLenIndex);

            int end = start + packageLen;
            if (end >= in.writerIndex()) {
                log.debug("read less min length of TinyPB package (26)");
                from = start + 1;
                continue;
            }

            if (in.getByte(end) != TinyPBProtocol.getPbEnd()) {
                log.debug("read end index is not PbEnd(0x03)");
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
                request.setErrCode(TinyPBErrorCode.ERROR_FAILED_DECODE.ordinal());
                request.setErrInfo(String.format("read pkLen [%d] out of range", packageLen));
                out.add(request);
                continue;
            }

            // index of msgReqLen
            int msgReqLenIndex = packageLenIndex + 4;
            int msgReqLen = in.getInt(msgReqLenIndex);
            log.debug(String.format("read msgReqLen[%d]", msgReqLen));
            request.setMsgReqLen(msgReqLen);

            int msgReqIndex = msgReqLenIndex + 4;
            if (msgReqIndex + msgReqLen >= in.writerIndex()) {
                request.setErrCode(TinyPBErrorCode.ERROR_FAILED_DECODE.ordinal());
                request.setErrInfo(String.format("read msgReqLen [%d] out of range", msgReqLen));
                out.add(request);
                continue;
            }
            request.setMsgReq(String.valueOf(in.getCharSequence(msgReqIndex, msgReqLen, CharsetUtil.UTF_8)));

            int serviceNameLenIndex = msgReqIndex + msgReqLen;
            int serviceNameLen = in.getInt(serviceNameLenIndex);
            log.debug(String.format("read serviceNameLen[%d]", serviceNameLen));
            request.setServiceNameLen(serviceNameLen);

            int serviceNameIndex = serviceNameLenIndex + 4;
            if (serviceNameIndex + serviceNameLen >= in.writerIndex()) {
                request.setErrCode(TinyPBErrorCode.ERROR_FAILED_DECODE.ordinal());
                request.setErrInfo(String.format("read serviceNameLen [%d] out of range", serviceNameLen));
                out.add(request);
                continue;
            }
            request.setServiceName(String.valueOf(in.getCharSequence(serviceNameIndex, serviceNameLen, CharsetUtil.UTF_8)));

            int errCodeIndex = serviceNameIndex + serviceNameLen;
            request.setErrCode(in.getInt(errCodeIndex));

            int errInfoLenIndex = errCodeIndex + 4;
            int errInfoLen = in.getInt(errInfoLenIndex);
            request.setErrInfoLen(errInfoLen);

            int errInfoIndex = errInfoLenIndex + 4;
            request.setErrInfo(String.valueOf(in.getCharSequence(errInfoIndex, errInfoLen, CharsetUtil.UTF_8)));

            int pbDataIndex = errInfoIndex + errInfoLen;
            request.setPbData(String.valueOf(in.getCharSequence(pbDataIndex, end - pbDataIndex - 4, CharsetUtil.UTF_8)));

            int checkSumIndex = end - 4;
            request.setCheckSum(in.getInt(checkSumIndex));
        }

    }
}
