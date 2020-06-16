package org.cxl.thor.rpc.core.server.net;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.core.server.AbstractRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Request> {

    private final Logger log = LoggerFactory.getLogger(ServerChannelHandler.class);

    protected AbstractRequestHandler handler;

    public ServerChannelHandler(AbstractRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) {
        Response response = handler.handlerRequest(request);
        ctx.writeAndFlush(response).addListener((ChannelFutureListener) channelFuture
                -> log.debug("send response for request " + request.getRequestId()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server -> happen exception:[{}] ", cause);
        ctx.close();
    }

}
