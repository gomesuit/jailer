package jailer.web.zookeeper;

import jailer.core.JailerEncryption;
import jailer.core.JailerEncryptionImpl;
import jailer.core.JailerNonEncryption;
import jailer.core.ZookeeperTimeOutConf;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class JailerZookeeperCurator {
	private final CuratorFramework client;
	private final String connectString;
	private final JailerEncryption encryption = new JailerEncryptionImpl();
	
	// Timeout
	private static final int default_sessionTimeoutMs = 60 * 1000;
	private static final int default_connectionTimeoutMs = 15 * 1000;
	
	// ExponentialBackoffRetry
	private static final int default_baseSleepTimeMs = 1000;
	private static final int default_maxRetries = 3;
	
	public JailerZookeeperCurator(String connectString){
		this(connectString, getDefaultRetryPolicy(), getDefaultZookeeperTimeOutConf(), getDefaultWatcher());
	}
	
	private static RetryPolicy getDefaultRetryPolicy(){
		return new ExponentialBackoffRetry(default_baseSleepTimeMs, default_maxRetries);
	}
	
	private static ZookeeperTimeOutConf getDefaultZookeeperTimeOutConf(){
		return new ZookeeperTimeOutConf(default_sessionTimeoutMs, default_connectionTimeoutMs);
	}
	
	private static DefaultWatcher getDefaultWatcher(){
		return new DefaultWatcher();
	}
	
	public JailerZookeeperCurator(String connectString, RetryPolicy retryPolicy, ZookeeperTimeOutConf conf, Watcher watcher){
		this.connectString = connectString;
		this.client = CuratorFrameworkFactory.builder().
        connectString(connectString).
        sessionTimeoutMs(conf.getSessionTimeoutMs()).
        connectionTimeoutMs(conf.getConnectionTimeoutMs()).
        retryPolicy(retryPolicy).
        build();
		this.client.start();
	}

	public void createDataForPersistent(String path, String data) throws Exception {
		client.create().withMode(CreateMode.PERSISTENT).forPath(path, encryption.encode(data));
	}
	
	public String getData(String path) throws Exception{
		byte[] strByte = client.getData().forPath(path);
		//return new String(strByte, charset);
		return encryption.decoded(strByte);
	}
	
	public void setData(String path, String data) throws Exception{
		//client.setData().forPath(path, data.getBytes(charset));
		client.setData().forPath(path, encryption.encode(data));
	}
	
	public List<String> getChildren(String path) throws Exception{
		return client.getChildren().forPath(path);
	}

	public void createDataForEphemeral(String path, String data) throws Exception {
		client.create().withMode(CreateMode.EPHEMERAL).forPath(path, encryption.encode(data));
	}

	public String createDataForEphemeralSequential(String path, String data) throws Exception {
		return client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, encryption.encode(data));
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
	
	static class DefaultWatcher implements Watcher{
		@Override
		public void process(WatchedEvent event) {
			System.out.println("DefaultWatcher.process!");
		}
	}
	
	
}
