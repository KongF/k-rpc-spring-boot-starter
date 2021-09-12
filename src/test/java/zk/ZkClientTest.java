package zk;

import com.alibaba.fastjson.JSON;
import com.kong.rpc.model.ServiceObject;
import com.kong.rpc.registry.zookeeper.WatcherApi;
import com.kong.rpc.registry.zookeeper.ZookeeperRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.io.IOException;

import static com.kong.rpc.constants.KrpcConstant.PROTOCOL_JAVA;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/kong-rpc-client.xml"})
public class ZkClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClientTest.class);
    private ZookeeperRegistry zookeeperRegistry;

    {
        try {
            zookeeperRegistry = new ZookeeperRegistry("127.0.0.1:2181",8080,PROTOCOL_JAVA,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testRegister() throws Exception {

        ServiceObject serviceObject = new ServiceObject();
        serviceObject.setServiceName("testProvider");
        serviceObject.setClazz(this.getClass());
        serviceObject.setObject(new Object());
        zookeeperRegistry.register(serviceObject);
        ServiceObject serviceTObject = zookeeperRegistry.getServiceObject("testProvider");
        LOGGER.info(JSON.toJSONString("获取服务：。。。。"+JSON.toJSONString(serviceTObject)));
    }
    @Test
    public void testGetService() throws Exception {

        ServiceObject serviceObject = zookeeperRegistry.getServiceObject("testProvider");
        LOGGER.info(JSON.toJSONString(serviceObject));
    }
}
