package jailer.curator.sample;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;

public class Main {

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		//CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.33.11", retryPolicy);
		CuratorFramework client = CuratorFrameworkFactory.builder().namespace("AAA").
        connectString("192.168.33.11").
        sessionTimeoutMs(60 * 1000).
        connectionTimeoutMs(15 * 1000).
        retryPolicy(retryPolicy).
        build();
		//client.create().forPath("/my/path", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		client.getCuratorListenable().addListener(new Listener());
		client.start();
		client.create().withMode(CreateMode.PERSISTENT).forPath("/my", "".getBytes());
		client.checkExists().usingWatcher(new MyCuratorWatcher()).forPath("/my");
		//client.checkExists().watched().forPath("/my");
		client.delete().forPath("/my");
		//client.create().inBackground(new MyBackgroundCallback()).forPath("/bbb");
		Thread.sleep(1000);
		client.close();
	}
	
	private static class Listener implements CuratorListener{

		@Override
		public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
			// TODO Auto-generated method stub
			//if(event.getType() == CuratorEventType.CREATE){
				System.out.println("CuratorListener : " + event.getType());
			//}
		}
		
	}
	
	private static class MyBackgroundCallback implements BackgroundCallback{

		@Override
		public void processResult(CuratorFramework client, CuratorEvent event)
				throws Exception {
			// TODO Auto-generated method stub
			System.out.println("BackgroundCallback : " + event.getType());
		}
		
	}
	
	private static class MyCuratorWatcher implements CuratorWatcher{

		@Override
		public void process(WatchedEvent event) throws Exception {
			// TODO Auto-generated method stub
			System.out.println("CuratorWatcher : " + event.getType());
		}
		
	}

}
