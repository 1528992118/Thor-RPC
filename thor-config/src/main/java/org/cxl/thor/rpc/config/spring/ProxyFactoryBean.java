package org.cxl.thor.rpc.config.spring;

import org.cxl.thor.rpc.core.client.ClientProxyFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author cxl
 * @date 2020/6/11 21:36
 */
public class ProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> interfaceType;

    private ClientProxyFactory<T> proxy;

    @Override
    public T getObject() throws Exception {
        return proxy.getProxy(interfaceType);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<T> getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    public ClientProxyFactory<T> getProxy() {
        return proxy;
    }

    public void setProxy(ClientProxyFactory<T> proxy) {
        this.proxy = proxy;
    }
}
