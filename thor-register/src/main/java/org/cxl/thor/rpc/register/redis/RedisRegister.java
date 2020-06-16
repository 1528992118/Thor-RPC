package org.cxl.thor.rpc.register.redis;

import org.cxl.thor.rpc.common.constant.RpcParamConstants;
import org.cxl.thor.rpc.register.AbstractRegister;
import org.cxl.thor.rpc.register.Provider;
import org.cxl.thor.rpc.register.Registry;
import org.cxl.thor.rpc.register.redis.util.JedisPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;

import static org.cxl.thor.rpc.common.constant.CommonConstants.REGISTER;
import static org.cxl.thor.rpc.common.constant.CommonConstants.UNREGISTER;

/**
 * @author cxl
 * @Description: Redis实现服务注册
 * @date 2020/6/15 15:14
 */
public class RedisRegister extends AbstractRegister implements Registry {

    private final Logger log = LoggerFactory.getLogger(RedisRegister.class);

    private String address;

    public RedisRegister(String address) {
        //目前适配单机模式
        this.address = address;
    }

    @Override
    public void register(Provider provider) throws Exception {
        super.register(provider);
        this.exportService(provider);
    }

    @Override
    public void unRegister() throws Exception {
        Map<String, Provider> cache = getServiceCaChe();
        cache.forEach((k, v) -> {
            Jedis jedis = JedisPoolManager.getInstance().build(address).getResource();
            jedis.hdel(RpcParamConstants.getProviderPath(k), v.getURL().valueOf());
            jedis.publish(UNREGISTER, v.getURL().valueOf());
            cache.remove(k);
        });
    }


    private void exportService(Provider provider) throws Exception {
        String uri = provider.getURL().valueOf();
        String serverPath = RpcParamConstants.getProviderPath(provider.getServiceName());
        //创建父节点
        Jedis jedis = JedisPoolManager.getInstance().build(address).getResource();
        jedis.hset(serverPath, uri, System.currentTimeMillis() + "");
        //发布消息
        jedis.publish(REGISTER, uri);
        log.info("Register success, key:{},value:{}", serverPath, uri);
    }


}
