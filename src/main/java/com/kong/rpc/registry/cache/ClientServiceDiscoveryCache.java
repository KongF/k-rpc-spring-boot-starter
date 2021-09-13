package com.kong.rpc.registry.cache;

import com.kong.rpc.model.ServiceResourse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 服务发现缓存
 * @author 10285
 */
public class ClientServiceDiscoveryCache {
    private static final Map<String, List<ServiceResourse>> SERVER_MAP = new ConcurrentHashMap<>();
    public static final List<String> SERVICE_CLASS_NAMES = new ArrayList<>();

    public static void put(String serviceName,List<ServiceResourse> serviceResourseList){
        SERVER_MAP.put(serviceName,serviceResourseList);
    }

    /**
     * 移除指定值
     * @param serviceName
     * @param serviceResourse
     */
    public static void remove(String serviceName,ServiceResourse serviceResourse){
        SERVER_MAP.computeIfPresent(serviceName,(key,value)->value.stream().filter(o->!o.toString().equals(serviceResourse.toString())).collect(Collectors.toList()));
    }

    public static void removeAll(String serviceName){
        SERVER_MAP.remove(serviceName);
    }

    public static boolean isEmpty(String serviceName){
        return SERVER_MAP.get(serviceName) == null || SERVER_MAP.get(serviceName).size()<=0;
    }
    public static List<ServiceResourse> get(String serviceName){
        return SERVER_MAP.get(serviceName);
    }
}
