package com.zhd.regist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;


public class ServiceRegistor {
	private String serviceAddress;
	private static final int CONNECT_TIMEOUT=5000;
	private CountDownLatch latch = new CountDownLatch(1);
	public static final String ZK_REGISTRY_PATH = "/registry";//注册节点
    public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";//节点
	public ServiceRegistor(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}
	

	/**
	 * 连接
	 * @param serviceAddress
	 * @return
	 */
	private ZooKeeper connect(){
		ZooKeeper zk=null;
		try {
			zk = new ZooKeeper(serviceAddress, CONNECT_TIMEOUT, new Watcher() {
				//连接不成功一直等待
				public void process(WatchedEvent arg0) {
					if(arg0.getState()==Event.KeeperState.SyncConnected){
						latch.countDown();
					}
				}
			});
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zk;
	}
	/**
	 * 创建zookeeper链接
	 * 
	 * @param data
	 */
	public void register(String data) {
		if (data != null) {
			ZooKeeper zk = connect();
			if (zk != null) {
				createNode(zk, data);
			}
		}
	}


	private void createNode(ZooKeeper zk, String data) {
		byte[] bytes = data.getBytes();
		try {
			if (zk.exists(ZK_REGISTRY_PATH, null) == null) {
				zk.create(ZK_REGISTRY_PATH, null, Ids.OPEN_ACL_UNSAFE,
						CreateMode.PERSISTENT);
			}
			String path = zk.create(ZK_DATA_PATH, bytes,
					Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
	}
}
