package jailer.curator.sample;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Main {

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.33.11", retryPolicy);
		//client.create().forPath("/my/path", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		client.start();
		client.create().withMode(CreateMode.PERSISTENT).forPath("/my", "".getBytes());
		client.close();
	}

}
