package com.kong.rpc.controller;

import com.kong.rpc.annotation.InjectRpcService;
import com.kong.rpc.service.DemoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/kong-rpc-client.xml"})
public class DemoController {
    @InjectRpcService
    private DemoService demoService;
    @Test
    public void testRegister() throws Exception {
        System.out.println(demoService.testAnnotion());
    }
}
