package org.cxl.thor.rpc.config.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cxl
 * @Description: 服务提供者信息
 * @date 2020/6/11 15:45
 */
@ConfigurationProperties(prefix = "thor.server")
public class ServerProperties {

    private Integer port;

    private String host;

    private String serializer;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }
}
