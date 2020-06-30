package org.cxl.thor.rpc.core.client.pool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.cxl.thor.rpc.common.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cxl
 * @date 2020/6/19 15:58
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

    private final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        //将返回结果塞回队列中
        NettyClientPool.getInstance()
                .getQueueMap().get(msg.getRequestId()).put(msg);
        log.info("client -> channelRead :[{}]", msg.getRequestId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client -> happen exception:[{}] ", cause);
        ctx.close();
    }

}
