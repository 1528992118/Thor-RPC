package org.cxl.thor.rpc.config.spring.configuration;

import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.config.spring.SerializerUtil;
import org.cxl.thor.rpc.config.spring.properties.RegistryProperties;
import org.cxl.thor.rpc.config.spring.properties.ServerProperties;
import org.cxl.thor.rpc.core.server.JDKDynamicProxyHandler;
import org.cxl.thor.rpc.core.server.net.NettyRpcServer;
import org.cxl.thor.rpc.register.Provider;
import org.cxl.thor.rpc.register.Registry;
import org.cxl.thor.rpc.register.redis.RedisRegister;
import org.cxl.thor.rpc.register.zookeeper.ZookeeperRegister;
import org.cxl.thor.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;

import static org.cxl.thor.rpc.common.constant.CommonConstants.*;

/**
 * @author cxl
 * @Description: 注册配置类
 * @date 2020/6/11 15:36
 */
@Configuration
@EnableConfigurationProperties({RegistryProperties.class, ServerProperties.class})
public class ServerAutoConfiguration implements ApplicationContextAware, ApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(ServerAutoConfiguration.class);

    private ApplicationContext applicationContext;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private RegistryProperties registryProperties;

    private String host = LOCALHOST_VALUE;

    private int port = DEFAULT_SERVER_PORT;

    private Registry registry;

    private Serializer serializer;

    private NettyRpcServer nettyRpcServer;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Provider decorate(Object bean) {
        org.cxl.thor.rpc.config.spring.annotation.Provider annotation
                = bean.getClass().getAnnotation(org.cxl.thor.rpc.config.spring.annotation.Provider.class);
        URL url = new URL("thor", host, port);
        return new Provider(annotation.value().getName(), annotation.version(), annotation.value()
                , bean, url);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            onContextRefreshedEvent();
        } else if (event instanceof ContextClosedEvent) {
            onContextClosedEvent();
        }
    }

    private void onContextRefreshedEvent() {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(
                org.cxl.thor.rpc.config.spring.annotation.Provider.class);
        if (null == beanMap || beanMap.isEmpty()) {
            return;
        }
        export(beanMap);
        start();
    }

    private void export(Map<String, Object> serverBeanMap) {

        try {

            this.host = Optional.ofNullable(serverProperties.getHost())
                    .orElse(InetAddress.getLocalHost().getHostAddress());
            this.port = Optional.ofNullable(serverProperties.getPort())
                    .orElse(DEFAULT_SERVER_PORT);
            serializer = SerializerUtil.getSerializer(Optional.ofNullable(serverProperties.getSerializer())
                    .orElse(JAVA_SERIALIZATION));

            Optional.ofNullable(registryProperties.getAddress())
                    .orElse(DEFAULT_ZK_ADDRESS);

            if (registryProperties.getAddress().startsWith("zookeeper://")) {
                registry = new ZookeeperRegister(registryProperties.getAddress().replaceAll("zookeeper://", ""));
            } else if (registryProperties.getAddress().startsWith("redis")) {
                registry = new RedisRegister(registryProperties.getAddress());
            }

            for (Object object : serverBeanMap.values()) {
                try {
                    registry.register(decorate(object));
                    log.info("provider's service[{}] has been exported", object.getClass().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void start() {
        JDKDynamicProxyHandler requestHandler = new JDKDynamicProxyHandler(registry
                , serializer);
        nettyRpcServer = new NettyRpcServer(host + ":" + port
                , requestHandler);
        nettyRpcServer.start();
    }

    private void onContextClosedEvent() {
        try {
            if (null != registry)
                registry.unRegister();
            if (null != nettyRpcServer)
                nettyRpcServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
