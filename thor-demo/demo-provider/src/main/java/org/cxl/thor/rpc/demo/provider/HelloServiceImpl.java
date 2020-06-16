package org.cxl.thor.rpc.demo.provider;

import org.cxl.thor.rpc.config.spring.annotation.Provider;
import org.cxl.thor.rpc.demo.api.HelloService;

/**
 * @author cxl
 * @date 2020/6/11 22:33
 */
@Provider(value = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }
}
