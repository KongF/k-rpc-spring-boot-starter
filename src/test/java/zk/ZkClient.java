package zk;

import com.alibaba.fastjson.JSONObject;
import com.kong.rpc.registry.zookeeper.WatcherApi;
import com.kong.rpc.registry.zookeeper.ZkApi;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/kong-rpc-client.xml"})
public class ZkClient {

    private static final Logger logger = LoggerFactory.getLogger(ZkClient.class);


    @Resource
    private ZkApi zkApi;

    @Test
    public void testZkNode(){
        String path = "/zkClient";
        String data = "create a node";
        try {
            Map<String,Object> serverObject = new HashMap<>();
            serverObject.put("serverName","provider");
            serverObject.put("ip","192.168.0.113");
            serverObject.put("port","8082");
            serverObject.put("method","create");
            serverObject.put("paramter",serverObject.toString());

            Stat isExit = zkApi.exists(path,new WatcherApi());
            logger.info("check node is exit:"+isExit);
            if(isExit!=null){
                zkApi.updateNode(path,JSONObject.toJSONString(serverObject));
            }
            logger.info("create a node " + path);
            zkApi.createNode(path, data);

            String getData = zkApi.getData(path,new WatcherApi());
            logger.info("get data from node return data:" +getData);

        } catch (Exception e) {
            logger.error("【创建持久化节点异常】{},{},{}",path,data,e);
        }
    }
}
