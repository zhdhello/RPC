package com.zhd.discover;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;


public class ServiceDiscovery {
	private String serviceAddress;
	private static final int CONNECT_TIMEOUT=5000;
	private CountDownLatch latch = new CountDownLatch(1);
	private volatile List<String> dataList = new ArrayList<String>();
	public static final String ZK_REGISTRY_PATH = "/registry";//ע��ڵ�
    public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";//�ڵ�
	public ServiceDiscovery(String serviceAddress) {
		this.serviceAddress = serviceAddress;
		ZooKeeper zk = connect();
		if (zk != null) {
			watchNode(zk);
		}
	}
	
	/**
	 * ����
	 * @param zk
	 */
	private void watchNode(final ZooKeeper zk) {
		
		try {
			List<String> nodeList = zk.getChildren(ZK_REGISTRY_PATH, new Watcher() {
				
				public void process(WatchedEvent arg0) {
					if(arg0.getType()==Event.EventType.NodeChildrenChanged){
						watchNode(zk);
					}
				}
			});
			List<String> dataList = new ArrayList<String>();
			// ѭ���ӽڵ�
			for (String node : nodeList) {
				// ��ȡ�ڵ��еķ�������ַ
				byte[] bytes = zk.getData(ZK_REGISTRY_PATH + "/"
						+ node, false, null);
				// �洢��list��
				dataList.add(new String(bytes));
			}
			// ���ڵ���Ϣ��¼�ڳ�Ա����
			this.dataList = dataList;
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����
	 * @param serviceAddress
	 * @return
	 */
	private ZooKeeper connect(){
		ZooKeeper zk=null;
		try {
			zk = new ZooKeeper(serviceAddress, CONNECT_TIMEOUT, new Watcher() {
				//���Ӳ��ɹ�һֱ�ȴ�
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
	 * �����½ڵ�
	 * 
	 * @return
	 */
	public String discover() {
		String data = null;
		int size = dataList.size();
		// �����½ڵ㣬ʹ�ü���
		if (size > 0) {
			if (size == 1) {
				data = dataList.get(0);
			} else {
				data = dataList.get(ThreadLocalRandom.current().nextInt(size));
			}
		}
		return data;
	}
}
