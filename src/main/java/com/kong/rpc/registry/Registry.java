package com.kong.rpc.registry;

import com.kong.rpc.model.ServiceObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Registry {

    /**
     * 注册节点
     * @param so
     * @throws Exception
     */
    void register(ServiceObject so) throws Exception;

    /**
     * 获取节点
     * @param name
     * @return
     * @throws Exception
     */
    ServiceObject getServiceObject(String name)throws Exception;
}
