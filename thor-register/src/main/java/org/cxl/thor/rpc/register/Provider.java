package org.cxl.thor.rpc.register;

import org.cxl.thor.rpc.common.Node;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.common.constant.CommonConstants;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Provider implements Node {

    //接口包路径
    private String serviceName;
    //服务版本
    private String version;
    //目标接口
    private final Class<?> serviceInterfaceClass;
    //实现类，用于本地缓存
    private final Object serviceInstance;
    //服务提供者类中方法,用于对比服务
    //服务提供者详细信息，最终暴露至注册中心
    private final URL url;

    public Provider(String serviceName, String version, Class<?> serviceInterfaceClass
            , Object serviceInstance, URL url) {
        this.serviceName = serviceName;
        this.version = version;
        this.serviceInterfaceClass = serviceInterfaceClass;
        this.serviceInstance = serviceInstance;
        this.url = url;
        initMethod();
    }

    private void initMethod() {
        Method[] methodsToExport = this.serviceInterfaceClass.getMethods();
        Set<String> methods = new HashSet<>();
        for (Method method : methodsToExport) {
            methods.add(method.getName());
        }
        Map<String, String> parameters = new HashMap<>();
        parameters.put(CommonConstants.METHOD_KEY, methods.stream().collect(Collectors.joining(",")));
        parameters.put(CommonConstants.INTERFACE_KEY, serviceInterfaceClass.getName());
        parameters.put(CommonConstants.VERSION_KEY, version);
        url.setParameters(parameters);
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public Class<?> getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public Object getServiceInstance() {
        return serviceInstance;
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
