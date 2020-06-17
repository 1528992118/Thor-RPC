package org.cxl.thor.rpc.config.spring;

import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.common.rpc.EchoService;
import org.cxl.thor.rpc.common.utils.CollectionUtils;
import org.cxl.thor.rpc.core.client.ClientInvocationHandler;
import org.cxl.thor.rpc.core.client.net.NetClient;
import org.cxl.thor.rpc.core.client.net.NettyRpcClient;
import org.cxl.thor.rpc.core.loadbalance.RandomLoadBalance;
import org.cxl.thor.rpc.register.LoadBalance;
import org.cxl.thor.rpc.register.ServiceDiscovery;
import org.cxl.thor.rpc.register.redis.RedisServerDiscovery;
import org.cxl.thor.rpc.register.zookeeper.ZookeeperServiceDiscovery;
import org.cxl.thor.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cxl
 * @Description: 客户端代理执行类
 * @date 2020/6/17 15:40
 */
public class ClientProxyContext<T> {

    private final Logger log = LoggerFactory.getLogger(ClientProxyContext.class);

    private NetClient netClient;

    private ServiceDiscovery serviceDiscovery;

    private Map<Class<?>, Object> serviceCaChe = new ConcurrentHashMap<>();

    private ClientProxyContext() {
    }

    private static class ClientProxyContextHolder {
        private static final ClientProxyContext INSTANCE = new ClientProxyContext();
    }

    public static ClientProxyContext getInstance() {
        return ClientProxyContextHolder.INSTANCE;
    }

    public void load(String address, String serializeProtocol) throws Exception {
        if (null != netClient && null != serviceDiscovery) {
            return;
        }
        LoadBalance loadBalance = new RandomLoadBalance();
        Serializer serializer = SerializerUtil.getSerializer(serializeProtocol);
        if (address.startsWith("zookeeper://")) {
            this.serviceDiscovery = new ZookeeperServiceDiscovery(address.replaceAll("zookeeper://", "")
                    , loadBalance);
        } else if (address.startsWith("redis")) {
            this.serviceDiscovery = new RedisServerDiscovery(address, loadBalance);
        }
        this.netClient = new NettyRpcClient(serializer);
    }

    public void pingProviders(List<URL> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }
        for (URL url : urls) {
            Request request = Request.newBuilder()
                    .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .serviceName(EchoService.class.getName())
                    .methodName("$echo")
                    .parameterTypes(new Class[]{String.class})
                    .parameterValues(new String[]{"ping"}).build();
            try {
                netClient.send(request, url);
            } catch (Throwable throwable) {
                serviceDiscovery.getAddressCache().remove(url.valueOf());
                log.error("ping url:{} fail,cause:{}", url.valueOf(), throwable.getCause());
            }
        }
    }

    public void pingProviders(Class<?> inter) {
        pingProviders(serviceDiscovery.getAllServices(inter.getName()));
    }

    public <T> T getProxy(Class<?> inter) {
        T serviceInstance = (T) this.serviceCaChe.get(inter);
        if (null == serviceInstance) {
            //添加回声测试的接口类
            Class<?>[] interfaces = new Class<?>[]{EchoService.class, inter};
            serviceInstance = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                    , interfaces
                    , new ClientInvocationHandler(inter, netClient, serviceDiscovery));
            this.serviceCaChe.put(inter, serviceInstance);
        }
        return serviceInstance;
    }

}
