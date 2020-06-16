package org.cxl.thor.rpc.core;

/**
 * @author cxl
 * @Description: demo
 * @date 2020/6/4 16:48
 */
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String param) {
        return String.format("hi, %s !", param);
    }

    @Override
    public Boolean available() {
        return true;
    }

}
