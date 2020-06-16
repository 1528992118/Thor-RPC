package org.cxl.thor.rpc.register.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.util.Pool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author cxl
 * @date 2020/6/16 15:04
 */
public class RedisTest {

    @Test
    public void test() {

        final JedisPool jedisPool = new JedisPool("redis://localhost:6379/0");

        final Jedis subscriberJedis = jedisPool.getResource();
        final Jedis publisherJedis = jedisPool.getResource();

        final RedisListener subscriber = new RedisListener(jedisPool);

        new Thread(() -> {
            try {
                System.out.println("Subscribing to mychannel,this thread will be block");
                subscriberJedis.subscribe(subscriber, "mychannel");
                System.out.println("subscription ended");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Publisher(publisherJedis, "mychannel").startPublish();
        publisherJedis.close();

        subscriber.unsubscribe();
        subscriberJedis.close();

    }


    public class RedisListener extends JedisPubSub {

        private final Logger log = LoggerFactory.getLogger(RedisListener.class);

        private final Pool<Jedis> jedisPool;

        public RedisListener(Pool<Jedis> jedisPool) {
            this.jedisPool = jedisPool;
        }

        @Override
        public void onMessage(String channel, String message) {
            if (log.isInfoEnabled()) {
                log.info("redis event, channel:{},message:{}", channel, message);
            }
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
            onMessage(channel, message);
        }

    }


    public class Publisher {
        private Jedis publisherJedis;
        private String channel;

        public Publisher(Jedis publishJedis, String channel) {
            this.publisherJedis = publishJedis;
            this.channel = channel;
        }

        public void startPublish() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    System.out.println("请输入message:");
                    String line = reader.readLine();
                    if (!"quit".equals(line)) {
                        publisherJedis.publish(channel, line);
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
