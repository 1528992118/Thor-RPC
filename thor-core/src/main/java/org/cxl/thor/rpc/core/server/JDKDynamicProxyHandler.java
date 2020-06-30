package org.cxl.thor.rpc.core.server;

import org.cxl.thor.rpc.common.Request;
import org.cxl.thor.rpc.common.Response;
import org.cxl.thor.rpc.common.Status;
import org.cxl.thor.rpc.common.exception.ThorException;
import org.cxl.thor.rpc.register.Provider;
import org.cxl.thor.rpc.register.Registry;
import org.cxl.thor.rpc.serialize.Serializer;

import java.lang.reflect.Method;

import static org.cxl.thor.rpc.common.exception.ThorException.JDK_REFLECT_ERROR;

/**
 * JDK动态代理实现类
 */
public class JDKDynamicProxyHandler extends AbstractRequestHandler {

    public JDKDynamicProxyHandler(Registry registry, Serializer serializer) {
        super(registry, serializer);
    }

    @Override
    public Response handlerRequest(Request request) {
        //获取处理对象
        Provider provider = getRegistry().getProvider(request.getServiceName());
        Response response;

        //前置判断该方法是否是回声测试，若为回声测试，则直接返回入参
        if ("$echo".equals(request.getMethodName())) {
            return new Response(request.getRequestId(), Status.SUCCESS
                    , request.getParameterValues()[0]);
        }

        if (provider == null) {
            response = new Response(request.getRequestId(), Status.NOT_FOUND);
        } else {
            //利用反射调用
            try {
                Method method = provider.getServiceInterfaceClass().getMethod(request.getMethodName()
                        , request.getParameterTypes());
                Object obj = method.invoke(provider.getServiceInstance(), request.getParameterValues());
                response = new Response(request.getRequestId(), Status.SUCCESS, obj);
            } catch (Exception e) {
                response = new Response(request.getRequestId(), Status.ERROR
                        , new ThorException(JDK_REFLECT_ERROR, e.getCause()));
            }
        }
        return response;
    }

}
