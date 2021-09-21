package com.kong.rpc.registry.zookeeper.zkhelper;

import com.kong.rpc.registry.cache.ClientServiceDiscoveryCache;
import org.I0Itec.zkclient.IZkChildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZkChildListenerImpl implements IZkChildListener {
    private static Logger LOGGER = LoggerFactory.getLogger(ZkChildListenerImpl.class);

    @Override
    public void handleChildChange(String parentPath, List<String> childList) throws Exception{
        String[] arr = parentPath.split("/");
        ClientServiceDiscoveryCache.removeAll(arr[2]);
    }
}
