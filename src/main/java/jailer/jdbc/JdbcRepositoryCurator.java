package jailer.jdbc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import jailer.core.CommonUtil;
import jailer.core.PathManager;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.JailerDataSource;

public class JdbcRepositoryCurator {
	private final CuratorFramework client;
	private static final Charset charset = StandardCharsets.UTF_8;
	
	public JdbcRepositoryCurator(String host, int port){
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		this.client = CuratorFrameworkFactory.newClient(host + ":" + port, retryPolicy);
		this.client.getCuratorListenable().addListener(new Listener());
		this.client.start();
	}
	
	public void close(){
		client.close();
	}
	
	public JailerDataSource getJailerDataSource(DataSourceKey key) throws Exception{
		byte[] result = client.getData().forPath(PathManager.getDataSourcePath(key));
		return CommonUtil.jsonToObject(new String(result, charset), JailerDataSource.class);
	}
	
	public boolean isExistsConnectionNode(ConnectionKey key) throws Exception{
		Stat stat = client.checkExists().forPath(PathManager.getConnectionPath(key));
		
		if(stat != null){
			return true;
		}else{
			return false;
		}
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
	
	public void repairConnectionNode(ConnectionKey key, ConnectionInfo info) throws Exception{
		String data = CommonUtil.objectToJson(info);
		client.create().withMode(CreateMode.EPHEMERAL).forPath(PathManager.getConnectionPath(key), data.getBytes(charset));
	}
	
	public void deleteConnection(ConnectionKey key) throws Exception{
		client.delete().guaranteed().forPath(PathManager.getConnectionPath(key));
	}
	
	public void watchDataSource(DataSourceKey key, CuratorWatcher watcher) throws Exception{
		client.checkExists().usingWatcher(watcher).forPath(PathManager.getDataSourcePath(key));
	}
	
	public DataSourceKey getDataSourceKey(String uuid) throws Exception{
		byte[] result = client.getData().forPath(PathManager.getUuidPath(uuid));
		return CommonUtil.jsonToObject(new String(result, charset), DataSourceKey.class);
	}

	private class Listener implements CuratorListener{

		@Override
		public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
			// TODO Auto-generated method stub
			//if(event.getType() == CuratorEventType.CREATE){
				System.out.println("CuratorListener : " + event.getType());
			//}
		}
		
	}
}
