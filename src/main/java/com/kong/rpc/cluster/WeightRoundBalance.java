package com.kong.rpc.cluster;

import com.kong.rpc.annotation.RpcLoadBalance;
import com.kong.rpc.constants.KrpcConstant;
import com.kong.rpc.model.ServiceResourse;

import java.util.List;

/**
 * 权重负载均衡
 * @author 10285
 */
@RpcLoadBalance(KrpcConstant.BALANCE_WEIGHT_ROUND)
public class WeightRoundBalance implements LoadBalance {
    private static int index;
    @Override
    public ServiceResourse chooseOne(List<ServiceResourse> services) {
        int allWeight = services.stream().mapToInt(ServiceResourse::getWeight).sum();
        int number = (index++)%allWeight;
        for(ServiceResourse serviceResourse: services){
            if(serviceResourse.getWeight()>number){
                return serviceResourse;
            }
            number -= serviceResourse.getWeight();
        }
        return null;
    }
}
