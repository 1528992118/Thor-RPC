package org.cxl.thor.rpc.common.rpc;

/**
 * @author cxl
 * @Description: 用于回声测试，验证服务状态
 * @date 2020/6/17 10:54
 */
public interface EchoService {

    Object $echo(Object message);

}
