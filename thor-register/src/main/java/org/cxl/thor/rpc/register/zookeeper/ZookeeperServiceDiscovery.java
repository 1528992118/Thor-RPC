package org.cxl.thor.rpc.register.zookeeper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.cxl.thor.rpc.common.URL;
import org.cxl.thor.rpc.common.constant.RpcParamConstants;
import org.cxl.thor.rpc.common.utils.CollectionUtils;
import org.cxl.thor.rpc.common.utils.PropertiesUtils;
import org.cxl.thor.rpc.register.EventListener;
import org.cxl.thor.rpc.register.LoadBalance;
import org.cxl.thor.rpc.register.ServiceDiscovery;
import org.cxl.thor.rpc.register.zookeeper.util.CuratorFrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cxl
 * @Description: 基于Zk的服务发现
 * @date 2020/6/8 18:58
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery, EventListener<String> {

    private final Logger log = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private LoadBalance loadBalance;

    private CuratorFramework curatorFramework;

    //TODO 需改善为CAS操作
    private Set<String> addressCache = Collections.synchronizedSet(new HashSet<>());

    public ZookeeperServiceDiscovery(LoadBalance loadBalance) throws Exception {
        this.loadBalance = loadBalance;
        curatorFramework = CuratorFrameworkUtils.buildCuratorFramework(PropertiesUtils
                .getValue(RpcParamConstants.PROPERTY_ZK_ADDRESS));
    }

    public ZookeeperServiceDiscovery(String address, LoadBalance loadBalance) throws Exception {
        this.loadBalance = loadBalance;
        curatorFramework = CuratorFrameworkUtils.buildCuratorFramework(address);
    }

    @Override
    public URL getService(String name) throws Exception {
        if (!addressCache.isEmpty()) {
            return getURL();
        }
        String path = RpcParamConstants.getProviderPath(name);
        try {
            addressCache = Sets.newHashSet(curatorFramework.getChildren().forPath(path));
        } catch (Exception e) {
            log.error("fetch service fail from zk, cause:{}", e.getMessage());
        }
        //注册监听
        this.registerListener(path);
        return getURL();
    }

    @Override
    public List<URL> getAllServices(String name) {
        List<URL> services = Lists.newArrayList();
        String path = RpcParamConstants.getProviderPath(name);
        try {
            List<String> servicePaths = curatorFramework.getChildren().forPath(path);
            if (CollectionUtils.isEmpty(servicePaths)) {
                return services;
            }
            for (String service : servicePaths) {
                services.add(URL.valueOf(URLDecoder.decode(service, "UTF-8")));
            }
        } catch (Exception e) {
            log.error("fetch all services fail from zk, cause:{}", e.getMessage());
        }
        return services;
    }

    @Override
    public Set<String> getAddressCache() {
        return addressCache;
    }

    @Override
    public void registerListener(String path) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener childrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            addressCache = Sets.newHashSet(curatorFramework.getChildren().forPath(path));
            log.info("zk provider change,reload");
        };
        childrenCache.getListenable().addListener(childrenCacheListener);
        childrenCache.start();
    }

    private URL getURL() throws Exception {
        String uri = loadBalance.select(addressCache);
        uri = URLDecoder.decode(uri, "UTF-8");
        return URL.valueOf(uri);
    }

}
