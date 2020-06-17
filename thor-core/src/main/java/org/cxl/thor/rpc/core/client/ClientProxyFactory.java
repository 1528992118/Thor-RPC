package org.cxl.thor.rpc.core.client;

import org.cxl.thor.rpc.core.client.net.NetClient;
import org.cxl.thor.rpc.register.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cxl
 * @Description: 消费者调用工厂
 * @date 2020/6/8 19:59
 */
public class ClientProxyFactory<T> {

    private NetClient netClient;

    private ServiceDiscovery serviceDiscovery;

    private Map<Class<?>, Object> serviceInstanceCaChe = new ConcurrentHashMap<>();

    public ClientProxyFactory(NetClient netClient, ServiceDiscovery serviceDiscovery) {
        this.netClient = netClient;
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T getProxy(Class<?> inter) {
        T serviceInstance = (T) this.serviceInstanceCaChe.get(inter);
        if (null == serviceInstance) {
            serviceInstance = (T) Proxy.newProxyInstance(inter.getClassLoader()
                    , new Class<?>[]{inter}
                    , new ClientInvocationHandler(inter, netClient, serviceDiscovery));
            this.serviceInstanceCaChe.put(inter, serviceInstance);
        }
        return serviceInstance;
    }

}