package zk;

import com.alibaba.fastjson.JSON;
import com.kong.rpc.model.ServiceObject;
import com.kong.rpc.registry.Registry;
import com.kong.rpc.registry.zookeeper.ZookeeperRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static com.kong.rpc.constants.KrpcConstant.PROTOCOL_JAVA;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/kong-rpc-client.xml"})
public class ZkClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClientTest.class);
    @Autowired
    private Registry serverRegister;
    @Test
    public void testRegister() throws Exception {

        ServiceObject serviceObject = new ServiceObject("",this.getClass(),"");
        serviceObject.setServiceName("testProvider");
        serviceObject.setClazz(this.getClass());
        serviceObject.setObject(new Object());
        serverRegister.register(serviceObject);
        //zookeeperRegistry.register(serviceObject);
        //ServiceObject serviceTObject = zookeeperRegistry.getServiceObject("testProvider");
        LOGGER.info(JSON.toJSONString("获取服务：。。。。"+JSON.toJSONString("serviceTObject")));
    }
    @Test
    public void testGetService() throws Exception {

        //ServiceObject serviceObject = zookeeperRegistry.getServiceObject("testProvider");
        LOGGER.info(JSON.toJSONString("serviceObject"));
    }
}
