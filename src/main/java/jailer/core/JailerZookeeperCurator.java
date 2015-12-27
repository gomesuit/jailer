package jailer.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

public class JailerZookeeperCurator {
	private static final Charset charset = StandardCharsets.UTF_8;
	private final CuratorFramework client;
	private final String connectString;
	
	public JailerZookeeperCurator(String connectString){
		this(connectString, new DefaultWatcher());
	}
	
	public JailerZookeeperCurator(String connectString, Watcher watcher){
		this.connectString = connectString;
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		this.client = CuratorFrameworkFactory.builder().
        connectString(connectString).
        sessionTimeoutMs(6 * 1000).
        connectionTimeoutMs(5 * 1000).
        retryPolicy(retryPolicy).
        build();
		this.client.start();
	}

	public void createDataForPersistent(String path, String data) throws Exception {
		client.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes(charset));
	}
	
	public String getData(String path) throws Exception{
		byte[] strByte = client.getData().forPath(path);
		return new String(strByte, charset);
	}
	
	public void setData(String path, String data) throws Exception{
		client.setData().forPath(path, data.getBytes(charset));
	}
	
	public List<String> getChildren(String path) throws Exception{
		return client.getChildren().forPath(path);
	}

	public void createDataForEphemeral(String path, String data) throws Exception {
		client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes(charset));
	}

	public String createDataForEphemeralSequential(String path, String data) throws Exception {
		return client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data.getBytes(charset));
	}

	public void delete(String path) throws Exception {
		client.delete().guaranteed().forPath(path);
	}

	public void exists(String path, Watcher watcher) throws Exception {
		client.checkExists().usingWatcher(watcher).forPath(path);
	}
	
	public void close(){
		client.close();
	}

	public String getConnectString() {
		return connectString;
	}
	
	
}
