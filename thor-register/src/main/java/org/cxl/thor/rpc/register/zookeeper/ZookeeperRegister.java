package org.cxl.thor.rpc.register.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.cxl.thor.rpc.common.constant.RpcParamConstants;
import org.cxl.thor.rpc.common.utils.PropertiesUtils;
import org.cxl.thor.rpc.register.AbstractRegister;
import org.cxl.thor.rpc.register.Registry;
import org.cxl.thor.rpc.register.Provider;
import org.cxl.thor.rpc.register.zookeeper.util.CuratorFrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author cxl
 * @Description: Zookeeper实现注册服务
 * @date 2020/6/8 14:57
 */
public class ZookeeperRegister extends AbstractRegister implements Registry {

    private final Logger log = LoggerFactory.getLogger(ZookeeperRegister.class);

    private CuratorFramework curatorFramework;

    private final ConnectionStateListener connectionStateListener = (curatorFramework, connectionState) -> {
        if ((connectionState == ConnectionState.RECONNECTED) || (connectionState == ConnectionState.CONNECTED)) {
            try {
                log.debug("Re-registering due to reconnection");
                reRegisterServices();
            } catch (Exception e) {
                log.error("Could not re-register instances after reconnection", e);
            }
        }
    };

    public ZookeeperRegister() throws Exception {
        curatorFramework = CuratorFrameworkUtils.buildCuratorFramework(PropertiesUtils
                .getValue(RpcParamConstants.PROPERTY_ZK_ADDRESS));
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
    }

    public ZookeeperRegister(String address) throws Exception {
        curatorFramework = CuratorFrameworkUtils.buildCuratorFramework(address);
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
    }


    @Override
    public void register(Provider provider) throws Exception {
        super.register(provider);
        this.exportService(provider);
    }

    @Override
    public void unRegister() {
        Map<String, Provider> cache = getServiceCaChe();
        cache.forEach((k, v) -> {
            String uri = v.getURL().valueOf();
            try {
                uri = URLEncoder.encode(uri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String fullPath = RpcParamConstants.getProviderPath(v.getServiceName())
                    .concat("/").concat(uri);
            try {
                curatorFramework.delete().guaranteed().forPath(fullPath);
            } catch (Exception e) {
                log.error("unRegister fail,due to:{}", e.getMessage());
            }
            cache.remove(k);
        });
    }

    private void exportService(Provider provider) throws Exception {
        String uri = provider.getURL().valueOf();
        uri = URLEncoder.encode(uri, "UTF-8");
        String serverPath = RpcParamConstants.getProviderPath(provider.getServiceName());
        //创建父节点
        if (curatorFramework.checkExists().forPath(serverPath) == null) {
            curatorFramework.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT).forPath(serverPath, "0".getBytes());
        }
        String address = serverPath.concat("/").concat(uri);
        //创建子节点
        if (curatorFramework.checkExists().forPath(address) != null) {
            curatorFramework.delete().deletingChildrenIfNeeded().forPath(address);
        }
        String node = curatorFramework.create().withMode(CreateMode.EPHEMERAL)
                .forPath(address, provider.getURL().getHost().getBytes());
        log.info("Register success, node:{}", node);
    }


    private void reRegisterServices() {
        Map<String, Provider> cache = getServiceCaChe();
        if (cache.isEmpty()) {
            return;
        }
        cache.forEach((k, v) -> {
            try {
                super.register(v);
                this.exportService(v);
            } catch (Exception e) {
                log.info("RegisterServices fail, due to:{}", e.getMessage());
            }
        });
    }


}
