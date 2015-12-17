package jailer.jdbc;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jailer.core.CommonUtil;
import jailer.core.JailerZookeeper;
import jailer.core.PathManager;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.JailerDataSource;

public class JdbcRepository {
	private final JailerZookeeper zooKeeper;
	
	public JdbcRepository(String host, int port) throws IOException{
		this.zooKeeper = new JailerZookeeper(host, port);
	}
	
	public void close() throws InterruptedException{
		zooKeeper.close();
	}
	
	public JailerDataSource getJailerDataSource(DataSourceKey key) throws JsonParseException, JsonMappingException, IOException, KeeperException, InterruptedException{
		String result = zooKeeper.getData(PathManager.getDataSourcePath(key));
		return CommonUtil.jsonToObject(result, JailerDataSource.class);
	}
	
	public ConnectionKey registConnection(DataSourceKey key, ConnectionInfo info) throws JsonProcessingException, KeeperException, InterruptedException{
		String data = CommonUtil.objectToJson(info);
		String connectionPath = zooKeeper.createDataForEphemeralSequential(PathManager.getDataSourcePath(key) + "/", data);
		
		ConnectionKey connectionKey = new ConnectionKey();
		connectionKey.setServiceId(key.getServiceId());
		connectionKey.setGroupId(key.getGroupId());
		connectionKey.setDataSourceId(key.getDataSourceId());
		connectionKey.setConnectionId(connectionPath.substring(connectionPath.length() - 10, connectionPath.length()));
		
		return connectionKey;
	}
	
	public void deleteConnection(ConnectionKey key) throws InterruptedException, KeeperException{
		zooKeeper.delete(PathManager.getConnectionPath(key));
	}
	
	public void watchDataSource(DataSourceKey key, Watcher watcher) throws KeeperException, InterruptedException{
		zooKeeper.exists(PathManager.getDataSourcePath(key), watcher);
	}
	
	public DataSourceKey getDataSourceKey(String uuid) throws KeeperException, InterruptedException, JsonParseException, JsonMappingException, IOException{
		String result = zooKeeper.getData(PathManager.getUuidPath(uuid));
		return CommonUtil.jsonToObject(result, DataSourceKey.class);
	}
}
