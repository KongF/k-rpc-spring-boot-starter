package com.kong.rpc.io.client;

import com.kong.rpc.model.ServiceResourse;
import org.apache.zookeeper.KeeperException;

import java.util.List;

public interface ServerDiscovery {
    /**
     * 服务发现
     * @param name
     * @return
     */
    public List<ServiceResourse> getServiceList(String name) throws KeeperException, InterruptedException;
}
