package org.cxl.thor.rpc.demo.consume;

import org.cxl.thor.rpc.config.spring.annotation.Consume;
import org.cxl.thor.rpc.demo.api.HelloService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cxl
 * @date 2020/6/11 22:23
 */
@RestController
@SpringBootApplication
public class ConsumeApplication {

    @Consume
    private HelloService helloService;

    @GetMapping(value = "/hello/{name}/test")
    public String sayHello(@PathVariable("name") String name) {
        return helloService.sayHello(name);
    }


    @GetMapping(value = "/pressure/test")
    public double pressureTest(@RequestParam("times") int times) {

        StopWatch sw = new StopWatch();
        sw.start();

        for (int i = 0; i < times; i++) {
            helloService.sayHello("test");
        }
        sw.stop();

        return sw.getTotalTimeSeconds();

    }


    public static void main(String[] args) {
        SpringApplication.run(ConsumeApplication.class, args);
    }


}
