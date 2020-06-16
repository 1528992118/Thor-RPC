package org.cxl.thor.rpc.common.constant;

import static org.cxl.thor.rpc.common.constant.CommonConstants.PROVIDER_KEY;

/**
 * @author cxl
 * @Description: 常量类
 * @date 2020/6/4 15:53
 */
public interface RpcParamConstants {

    String ROOT_PATH = "/thor";

    String ZK_SERVICE_PATH = "/service";

    String PROPERTY_ZK_ADDRESS = "zk.address";

    String PROPERTY_RPC_PORT = "rpc.port";

    String PROPERTY_RPC_PROTOCOL = "rpc.protocol";

    String PROPERTY_FILE_NAME = "app.properties";

    String SERIALIZER_CHARSET = "UTF-8";

    static String getProviderPath(String serviceName) {
        return RpcParamConstants.ROOT_PATH.concat("/")
                .concat(serviceName).concat("/")
                .concat(PROVIDER_KEY);
    }

}
