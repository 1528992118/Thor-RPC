package org.cxl.thor.rpc.demo.consume.test;

import org.cxl.thor.rpc.common.rpc.EchoService;
import org.cxl.thor.rpc.config.spring.annotation.Consume;
import org.cxl.thor.rpc.demo.api.HelloService;
import org.cxl.thor.rpc.demo.consume.ConsumeApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author cxl
 * @date 2020/6/12 17:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ConsumeApplication.class})
public class ConsumeTest {

    @Consume
    HelloService helloService;

    @Test
    public void test() throws IOException, InterruptedException {
        Object o = ((EchoService) helloService).$echo("111");
        System.out.print("############################" + o.toString());
    }

}
