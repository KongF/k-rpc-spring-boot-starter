package com.kong.rpc.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.kong.rpc.model.ServiceURL;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * Zookeeper 注册中心
 * @author kong
 */
public class ZookeeperRegistry {
    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER   = LoggerFactory.getLogger(ZookeeperRegistry.class);
    /**
     * Zookeeper zkClient
     */
    private ZkApi zkClient;

    private String UTF_8 = "utf-8";
    private void createZookeeperServiceNode(ServiceURL serviceURL){
        String urlJson = JSON.toJSONString(serviceURL);
        try{
            urlJson = URLEncoder.encode(urlJson,UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportEncodingEncodingException utf-8 encode",e);
        }
        String servicePath = "";//ServiceUtils.getRegisterServiceParent
        if(zkClient.exists(servicePath,false)!=null){
            zkClient.createNode(servicePath,JSON.toJSONString(serviceURL));
        }


    }
}
