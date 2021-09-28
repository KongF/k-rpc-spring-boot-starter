package com.kong.rpc.registry;

import com.kong.rpc.annotation.InjectRpcService;
import com.kong.rpc.annotation.RpcService;
import com.kong.rpc.constants.KrpcConstant;
import com.kong.rpc.io.ClientProxyFactory;
import com.kong.rpc.io.client.ZookeeperServerDiscovery;
import com.kong.rpc.model.ServiceObject;
import com.kong.rpc.registry.cache.ClientServiceDiscoveryCache;
import com.kong.rpc.registry.zookeeper.zkhelper.ZkChildListenerImpl;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * @author 10285
 */
public class RpcProcessor implements ApplicationListener<ContextRefreshedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RpcProcessor.class);

    private ClientProxyFactory clientProxyFactory;

    private Registry registry;

    private RpcServer rpcServer;

    public RpcProcessor(ClientProxyFactory clientProxyFactory, Registry registry, RpcServer rpcServer) {
        this.clientProxyFactory=clientProxyFactory;
        this.registry = registry;
        this.rpcServer=rpcServer;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //spring 启动后推送事件通知
        LOGGER.info("spring 启动完毕后发送事件");
        if(Objects.isNull(event.getApplicationContext().getParent())){
            ApplicationContext context = event.getApplicationContext();
            startServer(context);
            injectServices(context);
        }
    }
   private void startServer(ApplicationContext context){
        Map<String,Object> beans = context.getBeansWithAnnotation(RpcService.class);
        if(beans.size()>0){
            boolean isStartFlag = true;
            for(Object o : beans.values()){
                try{
                    Class<?> clazz = o.getClass();
                    Class<?>[] intefaces = clazz.getInterfaces();
                    ServiceObject serviceObject = null;
                    if(intefaces.length !=1){
                        RpcService rpcService = clazz.getAnnotation(RpcService.class);
                        String value = rpcService.value();
                        if("".equals(value)){
                            isStartFlag = false;
                            throw new UnsupportedOperationException("The exposed interface is not specific with '" + o.getClass().getName() + "'");
                        }
                        serviceObject = new ServiceObject(value,Class.forName(value),o);
                    } else{
                        Class<?> supperClass = intefaces[0];
                        serviceObject = new ServiceObject(supperClass.getName(),supperClass,o);
                    }
                    registry.register(serviceObject);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(isStartFlag){
                rpcServer.start();
            }
        }
    }
    private void injectServices(ApplicationContext context){
        String[] beanNames = context.getBeanDefinitionNames();
        for(String name : beanNames){
            Class<?> clazz = context.getType(name);
            if(Objects.isNull(clazz)){
                continue;
            }
            Field[] declaedFields = clazz.getDeclaredFields();
            for(Field field : declaedFields){
                InjectRpcService injectRpcService = field.getAnnotation(InjectRpcService.class);
                if(injectRpcService == null){
                    continue;
                }
                Class<?> fieldClass = field.getType();
                Object object =context.getBean(name);
                field.setAccessible(true);
                try{
                    field.set(object,clientProxyFactory.getProxy(fieldClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                ClientServiceDiscoveryCache.SERVICE_CLASS_NAMES.add(fieldClass.getName());
            }
        }
        if(clientProxyFactory.getServerDiscovery() instanceof ZookeeperServerDiscovery){
            ZookeeperServerDiscovery discovery = (ZookeeperServerDiscovery) clientProxyFactory.getServerDiscovery();
            ZkClient zkClient = discovery.getZkClient();
            ClientServiceDiscoveryCache.SERVICE_CLASS_NAMES.forEach(name->
                    {
                        String servicePath = KrpcConstant.ZK_SERVICE_PATH+KrpcConstant.PATH_DELIMITER+name+"/service";
                        zkClient.subscribeChildChanges(servicePath,new ZkChildListenerImpl());
                    });
            LOGGER.info("subscribe service node sucessfully");
        }
    }
}
