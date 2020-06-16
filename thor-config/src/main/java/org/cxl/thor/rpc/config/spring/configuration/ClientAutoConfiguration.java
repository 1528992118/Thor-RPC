package org.cxl.thor.rpc.config.spring.configuration;

import org.cxl.thor.rpc.common.utils.StringUtils;
import org.cxl.thor.rpc.config.spring.ProxyFactoryBean;
import org.cxl.thor.rpc.config.spring.SerializerUtil;
import org.cxl.thor.rpc.config.spring.YmlUtil;
import org.cxl.thor.rpc.config.spring.annotation.Consume;
import org.cxl.thor.rpc.config.spring.properties.ConsumeProperties;
import org.cxl.thor.rpc.config.spring.properties.RegistryProperties;
import org.cxl.thor.rpc.core.client.ClientProxyFactory;
import org.cxl.thor.rpc.core.client.net.NettyRpcClient;
import org.cxl.thor.rpc.core.loadbalance.RandomLoadBalance;
import org.cxl.thor.rpc.register.LoadBalance;
import org.cxl.thor.rpc.register.ServiceDiscovery;
import org.cxl.thor.rpc.register.redis.RedisServerDiscovery;
import org.cxl.thor.rpc.register.zookeeper.ZookeeperServiceDiscovery;
import org.cxl.thor.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static org.cxl.thor.rpc.common.constant.CommonConstants.JAVA_SERIALIZATION;

/**
 * @author cxl
 * @date 2020/6/11 20:35
 */
@Configuration
public class ClientAutoConfiguration implements BeanDefinitionRegistryPostProcessor, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ClientAutoConfiguration.class);

    private Serializer serializer;

    private ClientProxyFactory clientProxyFactory;

    private ServiceDiscovery serviceDiscovery;

    private ConsumeProperties consumeProperties;

    private RegistryProperties registryProperties;

    private ConsumeProperties getConsumeProperties() {
        ConsumeProperties consumeProperties = new ConsumeProperties();
        consumeProperties.setScanPath(getYmlConfigurerUtil().getStrYmlVal("thor.consume.scanPath"));
        consumeProperties.setSerializer(getYmlConfigurerUtil().getStrYmlVal("thor.consume.serializer"));
        return consumeProperties;
    }

    private RegistryProperties getRegistryProperties() {
        RegistryProperties registryProperties = new RegistryProperties();
        registryProperties.setAddress(getYmlConfigurerUtil().getStrYmlVal("thor.register.address"));
        return registryProperties;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        if (StringUtils.isEmpty(consumeProperties.getScanPath())) {
            return;
        }
        //获取Consume注解修饰的代理bean
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> true); // 设置过滤条件，这里扫描所有
        Set<BeanDefinition> beanDefinitionSet = scanner.findCandidateComponents(consumeProperties.getScanPath()); // 扫描指定路径下的类
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            String beanClassName = beanDefinition.getBeanClassName(); // 得到class name
            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanClassName); // 得到Class对象
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Field[] fields = beanClass.getDeclaredFields(); // 获得该Class的多有field
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Consume consume = beanClass.getAnnotation(Consume.class);
                Class<?> fieldClass = field.getType(); // 获取该标识下的类的类型，用于生成相应proxy
                BeanDefinitionHolder holder = createBeanDefinition(fieldClass);
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, beanDefinitionRegistry);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    private BeanDefinitionHolder createBeanDefinition(Class<?> fieldClass) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
        String className = fieldClass.getName();
        // bean的name首字母小写，spring通过它来注入
        String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf('.') + 1));
        // 给ProxyFactoryBean字段赋值
        builder.addPropertyValue("interfaceType", fieldClass);
        builder.addPropertyValue("proxy", clientProxyFactory);
        return new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.consumeProperties = getConsumeProperties();
        this.registryProperties = getRegistryProperties();
        if (StringUtils.isEmpty(consumeProperties.getScanPath())) {
            return;
        }
        LoadBalance loadBalance = new RandomLoadBalance();
        serializer = SerializerUtil.getSerializer(Optional.ofNullable(consumeProperties.getSerializer()).orElse(JAVA_SERIALIZATION));
        //初始化代理工厂
        if (registryProperties.getAddress().startsWith("zookeeper://")) {
            serviceDiscovery = new ZookeeperServiceDiscovery(registryProperties.getAddress().replaceAll("zookeeper://", "")
                    , loadBalance);
        } else if (registryProperties.getAddress().startsWith("redis")) {
            serviceDiscovery = new RedisServerDiscovery(registryProperties.getAddress(), loadBalance);
        }

        clientProxyFactory = new ClientProxyFactory(new NettyRpcClient(serializer), serviceDiscovery);
    }


    private YmlUtil getYmlConfigurerUtil() {
        //1:加载配置文件
        Resource app = new ClassPathResource("application.yml");
        Resource appDev = new ClassPathResource("application-dev.yml");
        Resource appProd = new ClassPathResource("application-prod.yml");
        Resource appTest = new ClassPathResource("application-test.yml");
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        // 2:将加载的配置文件交给 YamlPropertiesFactoryBean
        yamlPropertiesFactoryBean.setResources(app);
        // 3：将yml转换成 key：val
        Properties properties = yamlPropertiesFactoryBean.getObject();
        String active = properties.getProperty("spring.profiles.active");
        if (StringUtils.isEmpty(active)) {
            //判断当前配置是什么环境
            if ("dev".equals(active)) {
                yamlPropertiesFactoryBean.setResources(app, appDev);
            } else if ("prod".equals(active)) {
                yamlPropertiesFactoryBean.setResources(app, appProd);
            } else if ("test".equals(active)) {
                yamlPropertiesFactoryBean.setResources(app, appTest);
            }
        }
        // 4: 将Properties 通过构造方法交给我们写的工具类
        return new YmlUtil(yamlPropertiesFactoryBean.getObject());
    }


}
