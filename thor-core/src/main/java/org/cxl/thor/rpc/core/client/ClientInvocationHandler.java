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
import java.util.UUID;

import static org.cxl.thor.rpc.common.exception.ThorException.NOT_FOUNT_SERVICE;

/**
 * @author cxl
 * @date 2020/6/17 20:23
 */
public class ClientInvocationHandler implements InvocationHandler {

    private Class<?> interFace;

    private NetClient netClient;

    private ServiceDiscovery serviceDiscovery;

    public ClientInvocationHandler(Class<?> interFace, NetClient netClient, ServiceDiscovery serviceDiscovery) {
        this.interFace = interFace;
        this.netClient = netClient;
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        synchronized (netClient) {
            if (method.getName().equals("toString")) {
                return proxy.getClass().toString();
            }

            if (method.getName().equals("hashCode")) {
                return 0;
            }

            //得到服务
            String serviceName = interFace.getName();
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
