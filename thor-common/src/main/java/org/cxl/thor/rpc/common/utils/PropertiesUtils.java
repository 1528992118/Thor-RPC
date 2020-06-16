package org.cxl.thor.rpc.common.utils;


import org.cxl.thor.rpc.common.constant.RpcParamConstants;

import java.io.IOException;
import java.util.Properties;

/**
 * @author cxl
 * @Description: 读取配置文件
 * @date 2020/6/4 14:18
 */
public class PropertiesUtils {

    private static Properties properties;

    static {

        properties = new Properties();
        try {
            properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(RpcParamConstants.PROPERTY_FILE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return (String) properties.get(key);
    }

}
