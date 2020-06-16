package org.cxl.thor.rpc.register.zookeeper.util;

import java.util.concurrent.TimeUnit;

/**
 * @author cxl
 * @Description: Curator参数
 * @date 2020/6/8 15:55
 */
public enum CuratorFrameworkParams {

    BASE_SLEEP_TIME("baseSleepTimeMs", 50),

    MAX_SLEEP("maxSleepMs", 500),

    BLOCK_UNTIL_CONNECTED_WAIT("blockUntilConnectedWait", 10),

    BLOCK_UNTIL_CONNECTED_UNIT("blockUntilConnectedUnit", TimeUnit.SECONDS),

    MAX_RETRIES("maxRetries", 10);


    private String name;

    private Object defaultValue;

    CuratorFrameworkParams(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }


}
