package org.cxl.thor.rpc.core;

import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.core.server.JDKDynamicProxyHandler;
import org.cxl.thor.rpc.core.server.net.NettyRpcServer;
import org.cxl.thor.rpc.register.Provider;
import org.cxl.thor.rpc.register.zookeeper.ZookeeperRegister;
import org.cxl.thor.rpc.serialize.HessianSerializer;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.HashMap;

public class ProviderTest {

    @Test
    public void testProvider() throws Exception {
        ZookeeperRegister zookeeperRegister = new ZookeeperRegister();
        //实例化实现类
        DemoService demoService = new DemoServiceImpl();
        //设置服务提供者基本参数
        URL url = new URL("thor", InetAddress.getLocalHost().getHostAddress(), 5605, new HashMap<>());
        Provider provider = new Provider(DemoService.class.getName(),"v1.0.0", DemoService.class
                , demoService, url);
        //注册服务
        zookeeperRegister.register(provider);
        //实例化JDK动态代理类
        JDKDynamicProxyHandler requestHandler = new JDKDynamicProxyHandler(zookeeperRegister, new HessianSerializer());
        //实例化并启动服务
        NettyRpcServer rpcServer = new NettyRpcServer(InetAddress.getLocalHost().getHostAddress() + ":" + url.getPort()
                , requestHandler);
        rpcServer.start();
        System.in.read();
        rpcServer.stop();

    }

}
