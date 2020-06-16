package org.cxl.thor.rpc.common;

import org.cxl.thor.rpc.common.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class URL implements Serializable {

    private static final long serialVersionUID = 2118008491581019524L;

    //协议
    private final String protocol;

    //ip地址
    private final String host;

    //端口
    private final int port;

    //服务信息
    private Map<String, String> parameters = new HashMap<>();

    public URL(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public URL(String protocol, String host, int port, Map<String, String> parameters) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.parameters = parameters;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String valueOf() {
        return protocol.concat("://")
                .concat(host).concat(":")
                .concat(port + "").concat("?")
                .concat(StringUtils.toQueryString(parameters));
    }

    public void setParameters(Map<String, String> parameters) {
        if (this.parameters.isEmpty()) {
            this.parameters = parameters;
        } else {
            this.parameters.putAll(parameters);
        }
    }

    public String getAddress() {
        return host + ":" + port;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public static URL valueOf(String url) {
        if (url == null || (url = url.trim()).length() == 0) {
            throw new IllegalArgumentException("url == null");
        }
        String protocol = "";
        String path = "";
        String version = "";
        String host = "";
        int port = 0;
        Map<String, String> parameters = null;
        int i = url.indexOf('?');
        if (i >= 0) {
            String[] parts = url.substring(i + 1).split("&");
            parameters = new HashMap<>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() > 0) {
                    int j = part.indexOf('=');
                    if (j >= 0) {
                        String key = part.substring(0, j);
                        String value = part.substring(j + 1);
                        parameters.put(key, value);
                    } else {
                        parameters.put(part, part);
                    }
                }
            }
            url = url.substring(0, i);
        }
        i = url.indexOf("://");
        if (i >= 0) {
            if (i == 0) {
                throw new IllegalStateException("url missing protocol: \"" + url + "\"");
            }
            protocol = url.substring(0, i);
            url = url.substring(i + 3);
        }
        i = url.lastIndexOf(':');
        if (i >= 0 && i < url.length() - 1) {
            port = Integer.parseInt(url.substring(i + 1));
            url = url.substring(0, i);
        }
        if (url.length() > 0) {
            host = url;
        }
        return new URL(protocol, host, port, parameters);
    }

}
