package org.cxl.thor.rpc.core;

import org.cxl.thor.rpc.core.client.ClientProxyFactory;
import org.cxl.thor.rpc.core.client.net.NettyRpcClient;
import org.cxl.thor.rpc.core.loadbalance.RandomLoadBalance;
import org.cxl.thor.rpc.register.LoadBalance;
import org.cxl.thor.rpc.register.zookeeper.ZookeeperServiceDiscovery;
import org.cxl.thor.rpc.serialize.HessianSerializer;
import org.junit.jupiter.api.Test;

public class ConsumerTest {

    @Test
    public void testConsumer() throws Exception {
        //负载均衡
        LoadBalance loadBalance = new RandomLoadBalance();
        //初始化代理工厂
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory(
                new NettyRpcClient(new HessianSerializer())
                , new ZookeeperServiceDiscovery(loadBalance));

        DemoService demoService = (DemoService) clientProxyFactory.getProxy(DemoService.class);

        long startTime = System.currentTimeMillis();

        demoService.available();

        /*for (int i = 0; i < 10; i++) {
            try {
                demoService.sayHello("world" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        //String hello = demoService.sayHello("world");

        long endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;

        System.out.println("执行时间为：" + excTime + "s");

    }

}
