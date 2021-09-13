package com.kong.rpc.io;

import com.kong.rpc.cluster.LoadBalance;
import com.kong.rpc.common.KrpcRequest;
import com.kong.rpc.common.KrpcResponse;
import com.kong.rpc.execption.KrpcException;
import com.kong.rpc.io.client.ServerDiscovery;
import com.kong.rpc.io.protocol.MessageProtocol;
import com.kong.rpc.model.ServiceResourse;
import com.kong.rpc.registry.cache.ClientServiceDiscoveryCache;
import org.apache.zookeeper.KeeperException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 客户端代理工厂，创建远程服务代理
 * 封装编组请求、请求发送、编组响应等操作
 *
 * @author 10285
 */
public class ClientProxyFactory {
    private ServerDiscovery serverDiscovery;
    private NetClient netClient;
    private Map<String, MessageProtocol> supportProtocols;
    private Map<Class<?>,Object> objectCache = new HashMap<>();
    private LoadBalance loadBalance;

    /**
     * 获取服务列表
     * @param serviceName
     * @return
     */
    private List<ServiceResourse> getServiceList(String serviceName) throws KeeperException, InterruptedException {
        List<ServiceResourse> serviceList;
        synchronized (serviceName){
            if(ClientServiceDiscoveryCache.isEmpty(serviceName)){
                serviceList = serverDiscovery.getServiceList(serviceName);
                if(serviceList == null || serviceList.size()<=0){
                    throw new KrpcException("No provider available!");
                }
                ClientServiceDiscoveryCache.put(serviceName,serviceList);
            }else {
                serviceList = ClientServiceDiscoveryCache.get(serviceName);
            }
        }
        return serviceList;
    }
    private class ClientInvocationHandler implements InvocationHandler {

        private Class<?> clazz;

        public ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return proxy.toString();
            }

            if (method.getName().equals("hashCode")) {
                return 0;
            }
            // 1.获得服务信息
            String serviceName = clazz.getName();
            List<ServiceResourse> services = getServiceList(serviceName);
            ServiceResourse service = loadBalance.chooseOne(services);
            // 2.构造request对象
            KrpcRequest request = new KrpcRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setServiceName(service.getName());
            request.setMethod(method.getName());
            request.setParameters(args);
            request.setParameterTypes(method.getParameterTypes());
            // 3.协议层编组
            MessageProtocol messageProtocol = supportProtocols.get(service.getProtocol());
            KrpcResponse response = netClient.sendRequest(request, service, messageProtocol);
            // 编组请求
//            byte[] reqData = messageProtocol.marshallingRequest(request);
//            // 4. 调用网络层发送请求
//            byte[] respData = netClient.sendRequest(reqData, service);
//
//            // 5. 解组响应消息
//            RpcResponse response = messageProtocol.unmarshallingResponse(respData);
            if (response == null){
                throw new KrpcException("the response is null");
            }
            // 6.结果处理
            if (response.getException() != null) {
                return response.getException();
            }

            return response.getReturnValue();
        }
    }
    public <T> T getProxy(Class<T> clazz){
        return (T) objectCache.computeIfAbsent(clazz, c-> Proxy.newProxyInstance(c.getClassLoader(),new Class[]{c},new ClientInvocationHandler(c)));
    }

    public ServerDiscovery getServerDiscovery() {
        return serverDiscovery;
    }

    public void setServerDiscovery(ServerDiscovery serverDiscovery) {
        this.serverDiscovery = serverDiscovery;
    }

    public NetClient getNetClient() {
        return netClient;
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }

    public Map<String, MessageProtocol> getSupportProtocols() {
        return supportProtocols;
    }

    public void setSupportProtocols(Map<String, MessageProtocol> supportProtocols) {
        this.supportProtocols = supportProtocols;
    }

    public Map<Class<?>, Object> getObjectCache() {
        return objectCache;
    }

    public void setObjectCache(Map<Class<?>, Object> objectCache) {
        this.objectCache = objectCache;
    }

    public LoadBalance getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }
}
