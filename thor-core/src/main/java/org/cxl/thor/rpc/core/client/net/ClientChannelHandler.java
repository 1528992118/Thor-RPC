package org.cxl.thor.rpc.core.client.net;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author cxl
 * @Description: 通过处理类
 * @date 2020/6/8 20:06
 */
public class ClientChannelHandler extends SimpleChannelInboundHandler<Response> {

    private final Logger log = LoggerFactory.getLogger(ClientChannelHandler.class);

    private Request request;
    private Object response = null;
    private CountDownLatch countDownLatch;

    public ClientChannelHandler(Request request) {
        this.request = request;
        countDownLatch = new CountDownLatch(1);
    }

    public Object responseData() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(request);
        log.info("client -> client send message:[{}]", request.getRequestId());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response resp) {
        response = resp;
        countDownLatch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client -> happen exception:[{}] ", cause);
        ctx.close();
    }

}
