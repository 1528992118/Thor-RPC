package org.cxl.thor.rpc.register.redis;

import com.google.common.collect.Lists;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.common.constant.RpcParamConstants;
import org.cxl.thor.rpc.register.EventListener;
import org.cxl.thor.rpc.register.LoadBalance;
import org.cxl.thor.rpc.register.RegisterCallBack;
import org.cxl.thor.rpc.register.ServiceDiscovery;
import org.cxl.thor.rpc.register.redis.util.JedisPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.cxl.thor.rpc.common.constant.CommonConstants.REGISTER;
import static org.cxl.thor.rpc.common.constant.CommonConstants.UNREGISTER;

/**
 * @author cxl
 * @Description: 基于Redis的服务发现
 * @date 2020/6/16 14:18
 */
public class RedisServerDiscovery implements ServiceDiscovery, EventListener<String> {

    private final Logger log = LoggerFactory.getLogger(RedisServerDiscovery.class);

    private LoadBalance loadBalance;

    private String address;

    private RedisListener redisListener;

    private Set<String> addressCache = Collections.synchronizedSet(new HashSet<>());

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    public RedisServerDiscovery(String address, LoadBalance loadBalance) {
        //目前适配单机模式
        this.address = address;
        this.loadBalance = loadBalance;
    }

    @Override
    public void registerListener(String s) throws Exception {
        redisListener = new RedisListener();
        JedisPoolManager.getInstance().build(address).getResource().subscribe(redisListener, REGISTER, UNREGISTER);
    }

    @Override
    public URL getService(String name) throws Exception {
        if (!addressCache.isEmpty()) {
            return getURL();
        }
        String path = RpcParamConstants.getProviderPath(name);
        Jedis jedis = JedisPoolManager.getInstance().build(address).getResource();
        Map<String, String> map = jedis.hgetAll(path);
        for (String key : map.keySet()) {
            addressCache.add(key);
        }
        threadPoolExecutor.submit(() -> {
            try {
                registerListener(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return getURL();
    }

    @Override
    public List<URL> getAllServices(String name) {
        List<URL> services = Lists.newArrayList();
        String path = RpcParamConstants.getProviderPath(name);
        Jedis jedis = JedisPoolManager.getInstance().build(address).getResource();
        Map<String, String> map = jedis.hgetAll(path);
        for (String key : map.keySet()) {
            services.add(URL.valueOf(key));
        }
        return services;
    }

    @Override
    public Set<String> getAddressCache() {
        return addressCache;
    }


    public class RedisListener extends JedisPubSub {

        private final Logger log = LoggerFactory.getLogger(RedisRegister.class);

        public RedisListener() {
        }

        @Override
        public void onMessage(String channel, String message) {
            log.info("redis event, channel:{},message:{}", channel, message);
            switch (channel) {
                case REGISTER:
                    addressCache.add(message);
                    break;
                case UNREGISTER:
                    addressCache.remove(message);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
            onMessage(channel, message);
        }

    }

    private URL getURL() throws Exception {
        String uri = loadBalance.select(addressCache);
        return URL.valueOf(uri);
    }


}
