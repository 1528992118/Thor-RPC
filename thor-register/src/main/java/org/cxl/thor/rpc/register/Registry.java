package org.cxl.thor.rpc.register;

/**
 * @author cxl
 * @Description: 服务注册
 * @date 2020/6/4 15:44
 */
public interface Registry {

    Provider getProvider(String name);

    void register(Provider provider) throws Exception;

    void unRegister() throws Exception;

}
