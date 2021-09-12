package com.kong.rpc.io.client;

import com.kong.rpc.model.ServiceResourse;

import java.util.List;

public interface ServerDiscory {
    /**
     * 服务发现
     * @param name
     * @return
     */
    public List<ServiceResourse> getServiceList(String name);
}
