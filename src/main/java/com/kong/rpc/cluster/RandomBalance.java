package com.kong.rpc.cluster;

import com.kong.rpc.annotation.RpcLoadBalance;
import com.kong.rpc.constants.KrpcConstant;
import com.kong.rpc.model.ServiceResourse;

import java.util.List;
import java.util.Random;

/**
 * 随机
 */
@RpcLoadBalance(KrpcConstant.BALANCE_RANDOM)
public class RandomBalance implements LoadBalance{
    private static Random random = new Random();
    @Override
    public ServiceResourse chooseOne(List<ServiceResourse> services) {
        return services.get(random.nextInt(services.size()));
    }
}
