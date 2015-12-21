package jailer.jdbc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import jailer.core.CommonUtil;
import jailer.core.PathManager;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.JailerDataSource;

public class JdbcRepositoryCurator {
	private final CuratorFramework client;
	private static final Charset charset = StandardCharsets.UTF_8;
	
	public JdbcRepositoryCurator(String host, int port) throws IOException{
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		this.client = CuratorFrameworkFactory.newClient(host + ":" + port, retryPolicy);
		this.client.start();
	}
	
	public void close() throws InterruptedException{
		client.close();
	}
	
	public JailerDataSource getJailerDataSource(DataSourceKey key) throws Exception{
		byte[] result = client.getData().forPath(PathManager.getDataSourcePath(key));
		return CommonUtil.jsonToObject(new String(result, charset), JailerDataSource.class);
	}
	
	public ConnectionKey registConnection(DataSourceKey key, ConnectionInfo info) throws Exception{
		String data = CommonUtil.objectToJson(info);
		String connectionPath = client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(PathManager.getDataSourcePath(key) + "/", data.getBytes(charset));
		
		ConnectionKey connectionKey = new ConnectionKey();
		connectionKey.setServiceId(key.getServiceId());
		connectionKey.setGroupId(key.getGroupId());
		connectionKey.setDataSourceId(key.getDataSourceId());
		connectionKey.setConnectionId(connectionPath.substring(connectionPath.length() - 10, connectionPath.length()));
		
		return connectionKey;
	}
	
	public void deleteConnection(ConnectionKey key) throws Exception{
		client.delete().forPath(PathManager.getConnectionPath(key));
	}
	
	public void watchDataSource(DataSourceKey key, Watcher watcher) throws Exception{
		client.checkExists().usingWatcher(watcher).forPath(PathManager.getDataSourcePath(key));
	}
	
	public DataSourceKey getDataSourceKey(String uuid) throws Exception{
		byte[] result = client.getData().forPath(PathManager.getUuidPath(uuid));
		return CommonUtil.jsonToObject(new String(result, charset), DataSourceKey.class);
	}
}
