package com.kong.rpc.io.client;

import com.alibaba.fastjson.JSON;
import com.kong.rpc.constants.KrpcConstant;
import com.kong.rpc.model.ServiceResourse;
import com.kong.rpc.registry.zookeeper.WatcherApi;
import com.kong.rpc.registry.zookeeper.ZkClient;
import com.kong.rpc.registry.zookeeper.ZookeeperRegistry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZookeeperServerDiscovery implements ServerDiscovery {
    private final static Logger LOGGER   = LoggerFactory.getLogger(ZookeeperRegistry.class);
    private ZkClient zkClient;

    public ZookeeperServerDiscovery(String zkAddress,int sessionTimeout){
        zkClient = new ZkClient(zkAddress,sessionTimeout,new WatcherApi());
    }

    /**
     * 通过服务名获取服务列表
     * @param name
     * @return
     */
    @Override
    public List<ServiceResourse> getServiceList(String name) throws KeeperException, InterruptedException {
        String servicePath = KrpcConstant.ZK_SERVICE_PATH+KrpcConstant.PATH_DELIMITER+name+"/service";
        List<String> children = zkClient.getChildren(servicePath);
        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str->{
            String deCh = null;
            try{
                deCh = URLDecoder.decode(str,KrpcConstant.UTF_8);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("UnsupportedEncodingException utf8 encode",e);
            }
            return JSON.parseObject(deCh,ServiceResourse.class);
        }).collect(Collectors.toList());
    }
}
