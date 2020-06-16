package org.cxl.thor.rpc.register.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

/**
 * @author cxl
 * @Description: Jedis工具类
 * @date 2020/6/16 20:55
 */
public class JedisPoolManager {

    private Pool<Jedis> jedisPool;

    private String address;

    private static class JedisPoolManagerHolder {
        final static JedisPoolManager instance = new JedisPoolManager();
    }

    private JedisPoolManager() {
    }

    public static JedisPoolManager getInstance() {
        return JedisPoolManagerHolder.instance;
    }

    public Pool<Jedis> build(String address) {
        if (null != jedisPool) {
            return jedisPool;
        }
        jedisPool = new JedisPool(address);
        return jedisPool;
    }

}
