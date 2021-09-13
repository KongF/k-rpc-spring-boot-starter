package com.kong.rpc.registry;

import com.kong.rpc.io.ClientProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class RpcProcessor implements ApplicationListener<ContextRefreshedEvent> {
    private final static Logger LOGGER = LoggerFactory.getLogger(RpcProcessor.class);

    private ClientProxyFactory clientProxyFactory;

    private Registry registry;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //spring 启动后推送事件通知

    }
}
