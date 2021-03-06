package com.kong.rpc.config.spring.boot.autoconfigure;

import com.alibaba.fastjson.JSONObject;
import com.kong.rpc.annotation.RpcLoadBalance;
import com.kong.rpc.annotation.RpcMessageProtocol;
import com.kong.rpc.cluster.LoadBalance;
import com.kong.rpc.config.properties.RpcProperties;
import com.kong.rpc.execption.KrpcException;
import com.kong.rpc.io.ClientProxyFactory;
import com.kong.rpc.io.netty.NettyNetClient;
import com.kong.rpc.io.netty.NettyRpcServer;
import com.kong.rpc.io.client.ZookeeperServerDiscovery;
import com.kong.rpc.io.handler.RequestHandler;
import com.kong.rpc.io.protocol.MessageProtocol;
import com.kong.rpc.registry.Registry;
import com.kong.rpc.registry.RpcProcessor;
import com.kong.rpc.registry.RpcServer;
import com.kong.rpc.registry.zookeeper.ZookeeperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author k
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class KrpcAutoConfiguration {
    private static Logger LOGGER = LoggerFactory.getLogger(KrpcAutoConfiguration.class);

    @Bean
    public RpcProperties rpcProperties(){
        return new RpcProperties();
    }
    @Bean
    public Registry registry(@Autowired RpcProperties rpcProperties){
        return new ZookeeperRegistry(
                rpcProperties.getRegisterAddress(),
                rpcProperties.getServerPort(),
                rpcProperties.getProtocol(),
                rpcProperties.getWeight()
                );
    }
    @Bean
    public RequestHandler requestHandler(Registry registry,RpcProperties rpcProperties){
        return new RequestHandler(getMessagePropotocol(rpcProperties.getProtocol()),registry);
    }
    @Bean
    public RpcServer rpcServer(RequestHandler requestHandler,RpcProperties rpcProperties){
        return new NettyRpcServer(rpcProperties.getServerPort(),rpcProperties.getProtocol(),requestHandler);
    }
    @Bean
    public ClientProxyFactory clientProxyFactory(RpcProperties rpcProperties){
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory();
        clientProxyFactory.setServerDiscovery(new ZookeeperServerDiscovery(rpcProperties.getRegisterAddress()));
        //?????????????????????
        Map<String,MessageProtocol> supportMessageProtocols = buildSupporrtMessageProtocols();
        clientProxyFactory.setSupportProtocols(supportMessageProtocols);
        LoadBalance loadBalance = getLoadBalance(rpcProperties.getLoadBalance());
        clientProxyFactory.setLoadBalance(loadBalance);
        clientProxyFactory.setNetClient(new NettyNetClient());

        return clientProxyFactory;
    }

    public MessageProtocol getMessagePropotocol(String name){
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while(iterator.hasNext()){
            MessageProtocol messageProtocol = iterator.next();
            RpcMessageProtocol ano = messageProtocol.getClass().getAnnotation(RpcMessageProtocol.class);
            Assert.notNull(ano,"message protocol name can not be empty!");
            if(name.equals(ano.value())){
                return messageProtocol;
            }
        }
        throw new KrpcException("invalid messgae protocol config!"+name+JSONObject.toJSONString(loader));
    }

    @Bean
    public RpcProcessor rpcProcessor(ClientProxyFactory clientProxyFactory,Registry registry,RpcServer rpcServer){
        return new RpcProcessor(clientProxyFactory,registry,rpcServer);
    }
    private Map<String,MessageProtocol> buildSupporrtMessageProtocols(){
        Map<String,MessageProtocol> supportMessageProtocols = new HashMap<>(94);
        ServiceLoader<MessageProtocol> loader = ServiceLoader.load(MessageProtocol.class);
        Iterator<MessageProtocol> iterator = loader.iterator();
        while (iterator.hasNext()){
            MessageProtocol messageProtocol = iterator.next();
            RpcMessageProtocol ano = messageProtocol.getClass().getAnnotation(RpcMessageProtocol.class);
            Assert.notNull(ano,"message protocol name can not be empty!"+JSONObject.toJSONString(ano));
            supportMessageProtocols.put(ano.value(),messageProtocol);
        }
        return supportMessageProtocols;
    }

    private LoadBalance getLoadBalance(String name){
        ServiceLoader<LoadBalance> loader = ServiceLoader.load(LoadBalance.class);
        Iterator<LoadBalance> iterator = loader.iterator();
        while (iterator.hasNext()){
            LoadBalance loadBalance = iterator.next();
            RpcLoadBalance ano = loadBalance.getClass().getAnnotation(RpcLoadBalance.class);
            Assert.notNull(ano, "load balance name can not be empty!");
            if(name.equals(ano.value())){
                return loadBalance;
            }
        }
        throw new KrpcException("invalid load balance config");
    }
}
