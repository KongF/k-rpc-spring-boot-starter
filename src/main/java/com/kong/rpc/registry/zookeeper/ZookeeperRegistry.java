package com.kong.rpc.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.kong.rpc.model.ServiceObject;
import com.kong.rpc.model.ServiceResourse;
import com.kong.rpc.registry.DefaultRegistry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

import static com.kong.rpc.constants.KrpcConstant.*;


/**
 * Zookeeper 注册中心
 * @author kong
 */
public class ZookeeperRegistry extends DefaultRegistry {
    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER   = LoggerFactory.getLogger(ZookeeperRegistry.class);
    /**
     * Zookeeper zkClient
     */
    private ZkClient zkClient;

    public ZookeeperRegistry(String zkAddress, Integer port, String protocol, Integer weight) throws IOException {
        this.zkClient = new ZkClient(zkAddress,40000,new WatcherApi());
        //zkClient.setZkSerializer(new ZookeeperSerializer());
        this.port = port;
        this.protocol = protocol;
        this.weight = weight;
    }
    /**
     * 创建服务节点，暴露服务节点
     * @param serviceResourse
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void createZookeeperServiceNode(ServiceResourse serviceResourse) throws KeeperException, InterruptedException {
        String urlJson = JSON.toJSONString(serviceResourse);
        try {
            urlJson= URLEncoder.encode(urlJson,UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException utf8 encode",e);
        }
        String serviceName = serviceResourse.getName();
        String servicePath = ZK_SERVICE_PATH + PATH_DELIMITER + serviceName + "/service";
        //创建zookeeper服务永久节点
        if(!zkClient.exists(servicePath,true)){
            zkClient.createPersistent(servicePath,true);
        }
        String urlPath = servicePath+PATH_DELIMITER+urlJson;
        if(zkClient.exists(urlPath,true)){
            //删除之前节点
            zkClient.deleteNode(urlPath);
        }
        //创建临时节点，会话失效时清理节点
        zkClient.createEphemeral(urlPath);
    }
    /**
     * service register
     * 服务注册
     *
     * @param so
     * @throws Exception
     */
    @Override
    public void register(ServiceObject so) throws Exception {
        super.register(so);
        ServiceResourse serviceResourse = new ServiceResourse();
        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + port;
        serviceResourse.setAddress(address);
        serviceResourse.setName(so.getClazz().getName());
        serviceResourse.setProtocol(protocol);
        serviceResourse.setWeight(this.weight);
        this.createZookeeperServiceNode(serviceResourse);
    }

}
