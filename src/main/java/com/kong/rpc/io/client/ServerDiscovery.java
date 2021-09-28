package com.kong.rpc.io.client;

import com.kong.rpc.model.ServiceResourse;

import java.util.List;

public interface ServerDiscovery {
    /**
     * 服务发现
     * @param name
     * @return
     */
    List<ServiceResourse> getServiceList(String name);
}
