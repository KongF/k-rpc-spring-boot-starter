package com.kong.rpc.registry.zookeeper;

import com.kong.rpc.execption.zookeeper.ZkException;
import com.kong.rpc.execption.zookeeper.ZkNoNodeException;
import com.kong.rpc.execption.zookeeper.ZkNodeExistsException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@Component
public class ZkClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkClient.class);

    private ZooKeeper zooKeeper;
    public ZkClient() throws IOException {
        this.zooKeeper = new ZooKeeper("127.0.0.1:2181",4000,new WatcherApi());
    }
    public ZkClient(String connectString, int sessionTimeout, Watcher watcher) {
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            this.zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(Event.KeeperState.SyncConnected==event.getState()){
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            LOGGER.info("【初始化ZooKeeper连接状态....】={}",zooKeeper.getState());

        }catch (Exception e){
            LOGGER.error("初始化ZooKeeper连接异常....】={}",e);
        }
    }


    /**
     * 判断指定节点是否存在
     * @param path
     * @param needWatch  指定是否复用zookeeper中默认的Watcher
     * @return
     */
    public boolean exists(String path, boolean needWatch){
        try {
            return zooKeeper.exists(path,needWatch)!=null;
        } catch (Exception e) {
            LOGGER.error("【判断断指定节点是否存在异常】{},{}",path,e);
            return false;
        }
    }

    /**
     *  检测结点是否存在 并设置监听事件
     *      三种监听类型： 创建，删除，更新
     *
     * @param path
     * @param watcher  传入指定的监听类
     * @return
     */
    public Stat exists(String path, Watcher watcher ){
        try {
            return zooKeeper.exists(path,watcher);
        } catch (Exception e) {
            LOGGER.error("【断指定节点是否存在异常】{},{}",path,e);
            return null;
        }
    }


    /**
     * 创建持久化节点
     * @param path
     * @param data
     */
    public void createNode(String path, String data) throws InterruptedException, KeeperException {
        if(StringUtils.isEmpty(data)){
            zooKeeper.create(path,null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }else {
            zooKeeper.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }
    /**
     * Create a persistent node and set its ACLs.
     * 创建永久节点
     *
     * @param path
     * @throws IllegalArgumentException
     *             if called from anything except the ZooKeeper event thread
     * @throws RuntimeException
     *             if any other exception occurs
     */
    public void createPersistent(String path,boolean createParents) throws IllegalArgumentException, RuntimeException, InterruptedException, KeeperException {
        try {
            this.createNode(path,null);
        } catch (KeeperException.NoNodeException e) {
            if (!createParents) {
                throw e;
            }
            String parentDir = path.substring(0, path.lastIndexOf('/'));
            createPersistent(parentDir,createParents);
            createPersistent(path, createParents);
        }
    }

    /**
     * Create an ephemeral node.
     * 创建临时节点
     *
     * @param path
     * @throws IllegalArgumentException
     *             if called from anything except the ZooKeeper event thread
     * @throws RuntimeException
     *             if any other exception occurs
     */
    public void createEphemeral(final String path) throws IllegalArgumentException, RuntimeException, KeeperException, InterruptedException {
        zooKeeper. create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }


    /**
     * 修改持久化节点
     * @param path
     * @param data
     */
    public boolean updateNode(String path, String data){
        try {
            //zk的数据版本是从0开始计数的。如果客户端传入的是-1，则表示zk服务器需要基于最新的数据进行更新。如果对zk的数据节点的更新操作没有原子性要求则可以使用-1.
            //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            zooKeeper.setData(path,data.getBytes(),-1);
            return true;
        } catch (Exception e) {
            LOGGER.error("【修改持久化节点异常】{},{},{}",path,data,e);
            return false;
        }
    }

    /**
     * 删除持久化节点
     * @param path
     */
    public boolean deleteNode(String path){
        try {
            //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
            zooKeeper.delete(path,-1);
            return true;
        } catch (Exception e) {
            LOGGER.error("【删除持久化节点异常】{},{}",path,e);
            return false;
        }
    }

    /**
     * 获取当前节点的子节点(不包含孙子节点)
     * @param path 父节点path
     */
    public List<String> getChildren(String path) throws KeeperException, InterruptedException{
        List<String> list = zooKeeper.getChildren(path, false);
        return list;
    }

    /**
     * 获取指定节点的值
     * @param path
     * @return
     */
    public  String getData(String path,Watcher watcher){
        try {
            Stat stat=new Stat();
            byte[] bytes=zooKeeper.getData(path,watcher,stat);
            return  new String(bytes);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }


    /**
     * 测试方法  初始化
     */
    @PostConstruct
    public  void init(){
        String path="/zk-watcher-2";
        LOGGER.info("【执行初始化测试方法。。。。。。。。。。。。】");
        try {
            createNode(path,"测试");
            String value=getData(path,new WatcherApi());
            LOGGER.info("【执行初始化测试方法getData返回值。。。。。。。。。。。。】={}",value);

            // 删除节点出发 监听事件
            deleteNode(path);
        } catch (Exception e) {
            LOGGER.error("【执行初始化测试方法异常。。。。。。。。。。。。】");
        }
    }

}


