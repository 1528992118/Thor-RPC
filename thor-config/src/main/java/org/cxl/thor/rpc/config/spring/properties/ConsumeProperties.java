package org.cxl.thor.rpc.config.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cxl
 * @date 2020/6/11 21:29
 */
@ConfigurationProperties(prefix = "thor.consume")
public class ConsumeProperties {

    private String scanPath;

    private String serializer;

    public String getScanPath() {
        return scanPath;
    }

    public void setScanPath(String scanPath) {
        this.scanPath = scanPath;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }
}
