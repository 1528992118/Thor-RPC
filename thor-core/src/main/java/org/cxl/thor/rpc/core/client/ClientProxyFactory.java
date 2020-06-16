package org.cxl.thor.rpc.core.client;

import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.common.Status;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.common.exception.ThorException;
import org.cxl.thor.rpc.core.client.net.NetClient;
import org.cxl.thor.rpc.register.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.cxl.thor.rpc.common.exception.ThorException.NOT_FOUNT_SERVICE;

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
                    , new ClientStubInvocationHandler(inter));
            this.serviceInstanceCaChe.put(inter, serviceInstance);
        }
        return serviceInstance;
    }

    private class ClientStubInvocationHandler implements InvocationHandler {

        private Class<?> serviceInterFace;

        public ClientStubInvocationHandler(Class<?> serviceInterFace) {
            super();
            this.serviceInterFace = serviceInterFace;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getName().equals("toString")) {
                return proxy.getClass().toString();
            }

            if (method.getName().equals("hashCode")) {
                return 0;
            }

            //得到服务
            String serviceName = serviceInterFace.getName();
            URL url = serviceDiscovery.getService(serviceName);
            if (null == url) {
                throw new ThorException(NOT_FOUNT_SERVICE, "service not exist");
            }

            //构造请求信息
            Request request = Request.newBuilder()
                    .requestId(UUID.randomUUID().toString().replaceAll("-", ""))
                    .serviceName(serviceName)
                    .methodName(method.getName())
                    .parameterTypes(method.getParameterTypes())
                    .parameterValues(args).build();

            //消息解组
            Response response = netClient.send(request, url);

            if (!Status.SUCCESS.equals(response.getStatus())) {
                if (response.getResult() instanceof ThorException) {
                    throw (ThorException) response.getResult();
                }
                throw new ThorException(response.getStatus().getCode(), response.getMessage());
            }
            return response.getResult();
        }
    }


}
