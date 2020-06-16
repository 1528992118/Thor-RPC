package org.cxl.thor.rpc.core.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.cxl.thor.rpc.core.server.AbstractRequestHandler;
import org.cxl.thor.rpc.serialize.codec.NettyDecoder;
import org.cxl.thor.rpc.serialize.codec.NettyEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyRpcServer extends NetServer {

    private final Logger log = LoggerFactory.getLogger(NettyRpcServer.class);

    private Channel channel;

    public NettyRpcServer(String address, AbstractRequestHandler handler) {
        super(address, handler);
    }

    @Override
    public void start() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workLoopGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossLoopGroup, workLoopGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                /*.option(ChannelOption.TCP_NODELAY, true)*/
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                /*.childOption(ChannelOption.TCP_NODELAY, true)*/
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(65536, 0,
                                        4, 0, 0))
                                .addLast(new NettyDecoder(getHandler().getSerializer()))
                                .addLast(new NettyEncoder(getHandler().getSerializer()))
                                .addLast(new ServerChannelHandler(getHandler()));
                    }
                });

        try {

            String[] array = getAddress().split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            //启动服务
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            log.info("server -> bind[][] success", host, port);
            channel = future.channel();
            // 等待服务通道关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放线程组资源
            bossLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }

    }

    @Override
    public void stop() {
        this.channel.close();
    }


}
