# THOR-PRC

[![Build](https://img.shields.io/badge/build-netty-green.svg)]()&nbsp;[![Release](https://img.shields.io/badge/debug-1.0.0-blue.svg)]()&nbsp;[![Author](https://img.shields.io/badge/author-Xiaolong.Cao-yellow.svg)]()&nbsp;[![Time](https://img.shields.io/badge/time-2020.6.17-red.svg)]()&nbsp;

> 本项目参考了[Dubbo](http://dubbo.apache.org/en-us/)部分结构，并参考了一些基于`Netty`的`RPC`实现，没有过多分装，实现较为简单，提供大家一个思路，方便理解`RPC`相关实现。
> **适用初学并对`RPC`感兴趣的朋友，请在此基础上自由发挥**



## Features

* 基于`netty`网络通信
* 集成`java`和`hessian`序列化
* 基于`zoopker`和`redis`两种注册中心
* 提供`Random`的负载均衡，可扩展
* 集成`SpringBoot `快速启动
* 提供`echo`回声探测



## Quick Start

### Springboot 启动

#### 1.定义服务提供者接口

```java
public interface HelloService {
    String sayHello(String name);
}
```

#### 2.实现服务提供者接口

```java
@Provider(value = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }
}
```

#### 3. 启动服务提供者应用

```java
@SpringBootApplication
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }

}
```

##### 3.1 服务提供者配置文件

```yaml
server:
  port: 10004
thor:
  register:
    address: zookeeper://127.0.0.1:2181      //服务注册中心地址，可选 redis://localhost:6379/1，目前仅支持redis单机模式
  server:
    port: 10003                              //RPC服务端口
    host: 127.0.0.1                          //PRC服务IP,可不填自动取值
    serializer: hessian                      //netty序列化方式

logging:
  level:
    root: error
```

#### 4. 服务消费者调用

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConsumeApplication.class})
public class ConsumeTest {

    @Consume
    HelloService helloService;

    @Test
    public void test() {
        System.out.print(helloService.sayHello("lemon"));
    }

}
```

##### 4.1 服务消费者配置文件

```yaml
server:
  port: 10002
thor:
  register:
    address: zookeeper://127.0.0.1:2181      //服务注册中心地址，可选 redis://localhost:6379/1，目前仅支持redis单机模式
  consume:
    scanPath: org.cxl.thor.rpc.demo.consume  //扫描包路径
    serializer: hessian                      //netty序列化方式

logging:
  level:
    root: error
```

### 简单线程启动

####  1. 公共接口申明/服务实现

```java
public interface DemoService {
    String sayHello(String something);
}

public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String param) {
        return String.format("hi, %s !", param);
    }
}
```

#### 2. 启动Provider

```java
@Test
public void testProvider() throws Exception {
    ZookeeperRegister zookeeperRegister = new ZookeeperRegister("127.0.0.1:2181");
    //实例化实现类
    DemoService demoService = new DemoServiceImpl();
    //设置服务提供者基本参数
    URL url = new URL("thor", InetAddress.getLocalHost().getHostAddress(), 5605, new HashMap<>());
    Provider provider = new Provider(DemoService.class.getName(),"v1.0.0", DemoService.class
                                     , demoService, url);
    //注册服务
    zookeeperRegister.register(provider);
    //实例化JDK动态代理类
    JDKDynamicProxyHandler requestHandler = new JDKDynamicProxyHandler(zookeeperRegister, new HessianSerializer());
    //实例化并启动服务
    NettyRpcServer rpcServer = new NettyRpcServer(InetAddress.getLocalHost().getHostAddress() + ":" + url.getPort(),      
                                                  requestHandler);
    rpcServer.start();
    System.in.read();
    rpcServer.stop();
}
```

#### 3. 启动Comsumer

```java
@Test
public void testConsumer() throws Exception {
    //负载均衡
    LoadBalance loadBalance = new RandomLoadBalance();
    //初始化代理工厂
    ClientProxyFactory clientProxyFactory = new ClientProxyFactory(
               new NettyRpcClient(new HessianSerializer())
             , new ZookeeperServiceDiscovery("127.0.0.1:2181",loadBalance));

    DemoService demoService = (DemoService) clientProxyFactory.getProxy(DemoService.class);
    String hello = demoService.sayHello("world");
    System.out.println(hello);
}
```



## Build History

* **Thor-RPC 1.0.0 Build 2020/6/16**

  > 首版构建，基于`netty`网络框架，提供`zk`和`redis`两种注册中心，提供`java`和`hessian`序列化方式



## Contact

* Author: 15221393530@163.com
