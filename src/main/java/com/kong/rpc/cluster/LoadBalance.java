package com.kong.rpc.cluster;

import com.kong.rpc.model.ServiceResourse;

import java.util.List;

public interface LoadBalance {
    /**
     *
     * @param services
     * @return
     */
    ServiceResourse chooseOne(List<ServiceResourse> services);
}
