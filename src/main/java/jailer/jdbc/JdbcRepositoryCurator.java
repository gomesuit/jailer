package jailer.jdbc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.curator.CuratorConnectionLossException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
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
		//RetryPolicy retryPolicy = new RetryOneTime(1000);
		//this.client = CuratorFrameworkFactory.newClient(host + ":" + port, retryPolicy);
		this.client = CuratorFrameworkFactory.builder().
        connectString(host + ":" + port).
        sessionTimeoutMs(6 * 1000).
        connectionTimeoutMs(5 * 1000).
        retryPolicy(retryPolicy).
        build();
		this.client.getCuratorListenable().addListener(new Listener());
		this.client.getConnectionStateListenable().addListener(new MyConnectionStateListener());
		//this.client.getUnhandledErrorListenable().addListener(new MyUnhandledErrorListener());;
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
	
	private List<WatchDataSource> SessionExpiredWatcherList = new ArrayList<>();
	
	private class WatchDataSource{
		private DataSourceKey key;
		private CuratorWatcher watcher;
		
		private WatchDataSource(DataSourceKey key, CuratorWatcher watcher){
			this.key = key;
			this.watcher = watcher;
		}

		public DataSourceKey getKey() {
			return key;
		}

		public CuratorWatcher getWatcher() {
			return watcher;
		}
	}
	
	public void watchDataSource(DataSourceKey key, CuratorWatcher watcher) throws Exception{
		try{
			//client.checkExists().usingWatcher(watcher).forPath(PathManager.getDataSourcePath(key));
			//client.getUnhandledErrorListenable().addListener(new ConnectionLossListener(key, watcher));
			//client.getData().usingWatcher(watcher).forPath(PathManager.getDataSourcePath(key));
			client.getData().inBackground(new MyBackgroundCallback(key, watcher)).forPath(PathManager.getDataSourcePath(key));
		}catch(Exception e1){
			System.out.println("ExceptionExceptionExceptionExceptionExceptionExceptionExceptionExceptionExceptionExceptionExceptionExceptionException");
			e1.printStackTrace();
		}
	}
	
	private class MyBackgroundCallback implements BackgroundCallback{
		private DataSourceKey key;
		private CuratorWatcher watcher;
		
		public MyBackgroundCallback(DataSourceKey key, CuratorWatcher watcher){
			this.key = key;
			this.watcher = watcher;
		}

		@Override
		public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
			client.checkExists().usingWatcher(watcher).forPath(PathManager.getDataSourcePath(key));
			SessionExpiredWatcherList.add(new WatchDataSource(key, watcher));
		}
		
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
			//	System.out.println("CuratorListener : " + event.getType());
			//}
			System.out.println("CuratorListener event.getType() : " + event.getType());
			System.out.println("CuratorListener event.getPath() : " + event.getPath());
			System.out.println("CuratorListener event.getName() : " + event.getName());
			System.out.println("CuratorListener event.getWatchedEvent().getPath() : " + event.getWatchedEvent().getPath());
			
			System.out.println("CuratorListener event.getResultCode() : " + event.getResultCode());
			
		}
		
	}
	
	private class MyConnectionStateListener implements ConnectionStateListener{

		@Override
		public void stateChanged(CuratorFramework client, ConnectionState newState) {
			// TODO Auto-generated method stub
			System.out.println("ConnectionStateListener newState : " + newState);
			switch(newState){
			case RECONNECTED:
				for(WatchDataSource watchDataSource : SessionExpiredWatcherList){
					try {
						watchDataSource(watchDataSource.getKey(), watchDataSource.getWatcher());
						System.out.println("再ウォッチ");
					} catch (Exception e) {
						//発生しないはず
					}
				}
				SessionExpiredWatcherList.clear();
				break;
			default:
				break;
			}
			
		}
		
	}
	
	private class MyUnhandledErrorListener implements UnhandledErrorListener{

		@Override
		public void unhandledError(String message, Throwable e) {
			// TODO Auto-generated method stub
			System.out.println("UnhandledErrorListener message : " + message);
			System.out.println("UnhandledErrorListener e : " + e);
		}
		
	}
	
	private class ConnectionLossListener implements UnhandledErrorListener{
		private DataSourceKey key;
		private CuratorWatcher watcher;
		
		public ConnectionLossListener(DataSourceKey key, CuratorWatcher watcher){
			this.key = key;
			this.watcher = watcher;
		}

		@Override
		public void unhandledError(String message, Throwable e) {
			// TODO Auto-generated method stub
			//System.out.println("ConnectionLossListener message : " + message);
			//System.out.println("ConnectionLossListener e : " + e);
			//SessionExpiredWatcherList.add(new WatchDataSource(key, watcher));
		}
		
	}
	
}
