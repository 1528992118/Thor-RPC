/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cxl.thor.rpc.register.zookeeper.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

import static org.cxl.thor.rpc.register.zookeeper.util.CuratorFrameworkParams.*;

/**
 * Curator Framework Utilities Class
 *
 * @since 2.7.5
 */
public abstract class CuratorFrameworkUtils {

    public static CuratorFramework buildCuratorFramework(String address) throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(buildDefaultRetryPolicy())
                .build();
        curatorFramework.start();
        curatorFramework.blockUntilConnected((Integer) BLOCK_UNTIL_CONNECTED_WAIT.getDefaultValue(),
                (TimeUnit) BLOCK_UNTIL_CONNECTED_UNIT.getDefaultValue());
        return curatorFramework;
    }


    public static RetryPolicy buildDefaultRetryPolicy() {
        return new ExponentialBackoffRetry((Integer) BASE_SLEEP_TIME.getDefaultValue()
                , (Integer) MAX_RETRIES.getDefaultValue(), (Integer) MAX_SLEEP.getDefaultValue());
    }

}
