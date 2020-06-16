package org.cxl.thor.rpc.register;

/**
 * @author cxl
 * @Description: 监听服务状态
 * @date 2020/6/8 19:09
 */
public interface EventListener<T> {

    void registerListener(T t) throws Exception;

}
