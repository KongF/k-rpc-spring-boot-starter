package com.kong.rpc.registry;

import com.kong.rpc.model.ServiceObject;
import com.kong.rpc.registry.zookeeper.ZookeeperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class DefaultRegistry implements Registry {
    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER   = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private Map<String,ServiceObject> serviceMap = new HashMap<>();

    /**
     * 协议
     */
    protected String protocol;
    /**
     * 服务节点端口
     */
    protected Integer port;
    /**
     * 服务权重
     */
    protected Integer weight;
    @Override
    public void register(ServiceObject so) throws Exception {
        if (so == null){
            throw new IllegalArgumentException("parameter cannot be empty");
        }
        serviceMap.put(so.getServiceName(),so);
        LOGGER.info("serviceMap:"+serviceMap.toString()+".........");
    }

    @Override
    public ServiceObject getServiceObject(String name) throws Exception {
        LOGGER.info("serviceMap:"+serviceMap.toString()+".........");
        return serviceMap.get(name);
    }
}
