package org.cxl.thor.rpc.config.spring;

import java.util.Properties;

public class YmlUtil {

    private static Properties properties = new Properties();

    public YmlUtil(Properties properties) {
        this.properties = properties;
    }

    public static String getStrYmlVal(String key) {
        return properties.getProperty(key);
    }

    public static Integer getIntegerYmlVal(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

}
