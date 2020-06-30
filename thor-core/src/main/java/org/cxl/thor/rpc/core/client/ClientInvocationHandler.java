package org.cxl.thor.rpc.core.client;

import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.common.Status;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.common.exception.ThorException;
import org.cxl.thor.rpc.common.utils.CollectionUtils;
import org.cxl.thor.rpc.common.utils.StringUtils;
import org.cxl.thor.rpc.register.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.cxl.thor.rpc.common.constant.CommonConstants.DEFAULT_MAX_CLIENT_RETRY_TIMES;
import static org.cxl.thor.rpc.common.exception.ThorException.NOT_FOUNT_SERVICE;

/**
 * @author cxl
 * @date 2020/6/17 20:23
 */
public class ClientInvocationHandler implements InvocationHandler {

    private final Logger log = LoggerFactory.getLogger(ClientInvocationHandler.class);

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

        String name = method.getName();

        if (name.equals("toString")) {
            return proxy.getClass().toString();
        } else if (name.equals("hashCode")) {
            return System.identityHashCode(proxy);
        }

        Response response = this.send(0, interFace.getName()
                , method, null, args);

        if (!Status.SUCCESS.equals(response.getStatus())) {
            if (response.getResult() instanceof ThorException) {
                throw (ThorException) response.getResult();
            }
            throw new ThorException(response.getStatus().getCode(), response.getMessage());
        }
        return response.getResult();


    }


    private Response send(int retryTimes, String serviceName, Method method
            , String failAddress, Object[] args) throws Throwable {

        Response response = null;

        if (retryTimes >= DEFAULT_MAX_CLIENT_RETRY_TIMES) {
            return new Response(Status.ERROR);
        }

        if (!StringUtils.isBlank(failAddress)) {

            Set<String> addressCache = serviceDiscovery.getAddressCache();

            //踢除错误地址
            addressCache.remove(failAddress);

            List<URL> services = serviceDiscovery.getAllServices(serviceName);
            if (CollectionUtils.isEmpty(services) || CollectionUtils.isEmpty(addressCache)) {
                return new Response(Status.NOT_FOUND);
            }

        }

        //得到服务
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

        try {
            response = netClient.send(request, url);
        } catch (ExecutionException e) {
            retryTimes++;
            failAddress = url.valueOf();
            log.error("send fail,cause:{}", e.getMessage());
        }

        if (response == null) {
            return this.send(retryTimes, serviceName, method, failAddress, args);
        }

        return response;
    }


}
