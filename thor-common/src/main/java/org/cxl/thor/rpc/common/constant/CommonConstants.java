package org.cxl.thor.rpc.common.constant;

public interface CommonConstants {

    String JAVA_SERIALIZATION = "java";

    String F_JSON_SERIALIZATION = "f_json";

    String HESSIAN_SERIALIZATION = "hessian";

    int DEFAULT_SERVER_PORT = 30082;

    String DEFAULT_ZK_ADDRESS = "zookeeper://127.0.0.1:2181";

    String PROVIDER_KEY = "provider";

    String DELIMITER = "$_";

    int SYSTEM_PROPERTY_PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());

    int DEFAULT_MAX_CLIENT_RETRY_TIMES = 3;


    String UNDERLINE_SEPARATOR = "_";

    String SEPARATOR_REGEX = "_|-";


    String HIDE_KEY_PREFIX = ".";

    String DOT_REGEX = "\\.";

    String LOCALHOST_VALUE = "127.0.0.1";

    String GROUP_KEY = "group";

    String METHOD_KEY = "method";


    String INTERFACE_KEY = "interface";

    String VERSION_KEY = "version";

    String REGISTER = "register";

    String UNREGISTER = "unregister";


}
