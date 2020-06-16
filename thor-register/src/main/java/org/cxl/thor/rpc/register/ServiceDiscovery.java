package org.cxl.thor.rpc.register;

import org.cxl.thor.rpc.common.URL;

/**
 * @author cxl
 * @Description: 服务发现
 * @date 2020/6/8 17:04
 */
public interface ServiceDiscovery {

    URL getService(String name) throws Exception;

}
