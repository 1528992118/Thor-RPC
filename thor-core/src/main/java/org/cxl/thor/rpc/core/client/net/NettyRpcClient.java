package org.cxl.thor.rpc.core.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.serialize.Serializer;
import org.cxl.thor.rpc.serialize.codec.NettyDecoder;
import org.cxl.thor.rpc.serialize.codec.NettyEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cxl
 * @date 2020/6/8 20:02
 */
public class NettyRpcClient implements NetClient {

    private final Logger log = LoggerFactory.getLogger(NettyRpcClient.class);

    private Serializer serializer;

    public NettyRpcClient(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Response send(Request request, URL url) throws Throwable {
        final ClientChannelHandler clientChannelHandler = new ClientChannelHandler(request);
        Response response;
        //配置客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new NettyEncoder(serializer))
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0,
                                            4, 0, 0))
                                    .addLast(new NettyDecoder(serializer))
                                    .addLast(clientChannelHandler);
                        }
                    });

            //启动客户端
            b.connect(url.getHost(), url.getPort()).sync();
            response = (Response) clientChannelHandler.responseData();
            log.info("NettyRpcClient -> fetch response:[{}]", response);
        } finally {
            // 释放线程组资源
            group.shutdownGracefully();
        }
        return response;
    }

}
