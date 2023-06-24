package com.iker.tinyrpc.net.rpc.protobuf;

import com.google.protobuf.Message;
import com.iker.tinyrpc.net.future.RpcFuture;
import com.iker.tinyrpc.net.future.RpcSyncFuture;
import com.iker.tinyrpc.net.rpc.RpcFutureMap;
import com.iker.tinyrpc.util.SpringContextUtil;
import com.iker.tinyrpc.util.TinyRpcErrorCode;
import com.iker.tinyrpc.util.TinyRpcSystemException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * An async RpcChannel to do rpc, it will not be blocking
 */
@Slf4j
public class TinyRpcAsyncChannel extends AbstractProtobufRpcChannel {

    public TinyRpcAsyncChannel(InetSocketAddress peerAddr) {
        super(peerAddr);
    }

    @Getter
    private RpcSyncFuture replyFuture;

    public void sync() throws ExecutionException, InterruptedException {
        replyFuture.get();
    }

    private void registerFuture() {
        // 首先注册 future 对象。 无论成功或者失败都会调用 callback
        // 因此主调方需要在 callback 判断 rpc 调用是否成功
        String id = tcpClient.getChannel().id() + "-" + sendProtocol.getMsgReq();
        replyFuture = new RpcSyncFuture(id, ()-> {
            log.info("{}|{}|RpcCallBack, peer addr {}", sendProtocol.getMsgReq(), sendProtocol.getServiceName(), peerAddr.toString());

            if (tinyRpcController.failed()) {
                log.error("{}|{}|rpc failed, peer addr: {}, errorCode:{}, errorInfo: {}",
                        replyProtocol.getMsgReq(), replyProtocol.getServiceName(), peerAddr.toString(),
                        tinyRpcController.getErrCode().ordinal(), tinyRpcController.getErrInfo());
            } else {
                log.info("{}|{}|rpc success, peer addr: {}, total get replay {} bytes",
                        replyProtocol.getMsgReq(), replyProtocol.getServiceName(), peerAddr.toString(),
                        replyProtocol.getPkLen());
            }
            Optional.ofNullable(done).ifPresent(
                    (func) -> {
                        func.run(parseResponse(responsePrototype));
                    }
            );
            // invoke 最后，需要删除 future 对象，不然会内存泄漏
            SpringContextUtil.getBean(RpcFutureMap.class).deleteFuture(id);
        });

        // future 对象注册到全局 map 里面
        SpringContextUtil.getBean(RpcFutureMap.class).addFuture(replyFuture);
    }

    private void invokeFuture(TinyRpcErrorCode errorCode, String errorInfo) {
        log.error("{}|rpc failed, peer addr: {}, errorCode:{}, errorInfo: {}", sendProtocol.getMsgReq(), peerAddr.toString(), errorCode.ordinal(), errorInfo);
        tinyRpcController.setErrCode(errorCode);
        tinyRpcController.setErrInfo(errorInfo);
        replyProtocol.setErrInfo(errorInfo);
        replyProtocol.setErrCode(errorCode.ordinal());

        replyFuture.invoke(replyProtocol);
    }

    /**
     * send protocol and get reply at there, you can do it by yourself, such as:
     * 1. sync: send data, then blocking wait until get peer server's reply
     * 2. async: send data, and set callback, then do other things. callback will run when get peer server's reply
     */
    @Override
    protected void ioHandler() throws TinyRpcSystemException {
        asyncConnect().addListener( future -> {
            registerFuture();
            if (future.isSuccess()) {
                log.info("{}|connect to peer addr {} success", sendProtocol.getMsgReq(), peerAddr.toString());
                asyncSendMessage().addListener( future1 -> {
                    if (future1.isSuccess()) {
                        log.info("{}|send message to peer addr {} success, send total {} bytes", sendProtocol.getMsgReq(), peerAddr.toString(), sendProtocol.getPkLen());
                    } else {
                        log.error("{}|send message to peer addr {} failed", sendProtocol.getMsgReq(), peerAddr.toString());
                        // 发送包失败，直接唤醒 replyFuture，通知失败
                        invokeFuture(TinyRpcErrorCode.ERROR_SEND_MESSAGE, "send message, peer addr: " + peerAddr.toString());
                    }
               });
           } else {
                log.error("{}|connect to peer addr {} failed", sendProtocol.getMsgReq(), peerAddr.toString());
                // 连接失败，直接唤醒 replyFuture，通知失败
                invokeFuture(TinyRpcErrorCode.ERROR_FAILED_CONNECT, "connect error, peer addr: " + peerAddr.toString());
           }
        });
    }

}
