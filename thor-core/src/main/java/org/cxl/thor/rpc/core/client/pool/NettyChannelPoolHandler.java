package org.cxl.thor.rpc.core.client.pool;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.cxl.thor.rpc.serialize.Serializer;
import org.cxl.thor.rpc.serialize.codec.NettyDecoder;
import org.cxl.thor.rpc.serialize.codec.NettyEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author cxl
 * @date 2020/6/19 15:11
 */
public class NettyChannelPoolHandler implements ChannelPoolHandler {

    private final Logger log = LoggerFactory.getLogger(NettyChannelPoolHandler.class);

    private Serializer serializer;

    public NettyChannelPoolHandler(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        log.info("channelReleased. Chanel ID:" + ch.id());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        log.info("channelAcquired. Chanel ID:" + ch.id());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.pipeline()
               /*.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS))*/
                .addLast(new NettyEncoder(serializer))
                .addLast(new NettyDecoder(serializer))
                .addLast(new NettyClientHandler());
    }


}
