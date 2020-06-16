package org.cxl.thor.rpc.register;

import java.util.Set;

/**
 * @author cxl
 * @Description: 负载均衡
 * @date 2020/6/8 19:10
 */
public interface LoadBalance {

    String select(Set<String> providers);

}
