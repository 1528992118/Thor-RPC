package org.cxl.thor.rpc.core.client.pool;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.core.client.NetClient;
import org.cxl.thor.rpc.serialize.Serializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.cxl.thor.rpc.common.constant.CommonConstants.SYSTEM_PROPERTY_PARALLEL;

/**
 * @author cxl
 * @date 2020/6/19 15:08
 */
public class NettyClientPool implements NetClient {

    final Bootstrap strap = new Bootstrap();
    final EventLoopGroup group = new NioEventLoopGroup(SYSTEM_PROPERTY_PARALLEL * 2);

    //key为目标主机的InetSocketAddress对象，value为目标主机对应的连接池
    private ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;

    private Map<String, LinkedBlockingQueue> queueMap = new ConcurrentHashMap<>();

    private static class NettyRpcPoolClientHolder {
        private static final NettyClientPool INSTANCE = new NettyClientPool();
    }

    private NettyClientPool() {
    }

    public static NettyClientPool getInstance() {
        return NettyRpcPoolClientHolder.INSTANCE;
    }

    public Map<String, LinkedBlockingQueue> getQueueMap() {
        return this.queueMap;
    }

    public void load(Serializer serializer) throws Exception {

        strap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {

            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(strap.remoteAddress(key), new NettyChannelPoolHandler(serializer), 2);
            }

        };

    }

    @Override
    public Response send(Request request, URL url) throws Throwable {

        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(1);

        queueMap.put(request.getRequestId(), linkedBlockingQueue);

        InetSocketAddress address = new InetSocketAddress(url.getHost(), url.getPort());
        final SimpleChannelPool pool = this.poolMap.get(address);
        Future<Channel> channelFuture = pool.acquire();
        final Channel ch = channelFuture.get();
        ch.writeAndFlush(request);

        channelFuture.addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                pool.release(ch);
            }
        });

        //等待返回结果，默认超时时间1min
        Response response = (Response) queueMap.get(request.getRequestId())
                .poll(10, TimeUnit.SECONDS);

        queueMap.remove(request.getRequestId());
        return response;
    }


}
