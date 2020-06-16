package org.cxl.thor.rpc.register;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cxl
 * @Description: 默认注册实现
 * @date 2020/6/5 10:05
 */
public abstract class AbstractRegister implements Registry {

    private Map<String, Provider> serviceCaChe = new ConcurrentHashMap<>();

    public Provider getProvider(String name) {
        return serviceCaChe.get(name);
    }

    @Override
    public void register(Provider provider) throws Exception {
        if (null == provider) {
            throw new IllegalArgumentException("provider can't be null!");
        }
        serviceCaChe.putIfAbsent(provider.getServiceName(), provider);
    }

    protected Map<String, Provider> getServiceCaChe() {
        return this.serviceCaChe;
    }

}
