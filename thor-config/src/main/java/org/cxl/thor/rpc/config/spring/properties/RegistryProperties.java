package org.cxl.thor.rpc.config.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cxl
 * @Description: 服务提供者配置文件
 * @date 2020/6/11 15:17
 */
@ConfigurationProperties(prefix = "thor.register")
public class RegistryProperties {

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
