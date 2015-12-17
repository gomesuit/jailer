package jailer.web.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jailer.core.CommonUtil;
import jailer.core.JansibleZookeeper;
import jailer.core.PathManager;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Repository
public class ZookeeperRepository {
	@Autowired
	private JansibleZookeeper zooKeeper;
	
	public List<String> getServiceList() {
		try {
			return zooKeeper.getChildren(PathManager.getDataSourceRootPath());
		} catch (KeeperException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void registGroup(GroupKey key) throws KeeperException, InterruptedException{
		zooKeeper.createDataForPersistent(PathManager.getGroupPath(key), "");
	}

	public void deleteGroup(GroupKey key) throws InterruptedException, KeeperException{
		zooKeeper.delete(PathManager.getGroupPath(key));
	}

	public List<String> getGroupList(ServiceKey key){
		try {
			return zooKeeper.getChildren(PathManager.getServicePath(key));
		} catch (KeeperException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void registDataSource(DataSourceKey key, JailerDataSource dataSource) throws JsonProcessingException, KeeperException, InterruptedException{
		String data = CommonUtil.objectToJson(dataSource);
		zooKeeper.createDataForPersistent(PathManager.getDataSourcePath(key), data);
	}

	public void deleteDataSource(DataSourceKey key) throws InterruptedException, KeeperException{
		zooKeeper.delete(PathManager.getDataSourcePath(key));
	}

	public List<String> getDataSourceList(GroupKey key){
		try {
			return zooKeeper.getChildren(PathManager.getGroupPath(key));
		} catch (KeeperException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void updateDataSource(DataSourceKey key, JailerDataSource jailerDataSource) throws JsonProcessingException, KeeperException, InterruptedException {
		String data = CommonUtil.objectToJson(jailerDataSource);
		zooKeeper.setData(PathManager.getDataSourcePath(key), data);
	}

	public JailerDataSource getDataSource(DataSourceKey key) throws KeeperException, InterruptedException, JsonParseException, JsonMappingException, IOException {
		String result = zooKeeper.getData(PathManager.getDataSourcePath(key));
		return CommonUtil.jsonToObject(result, JailerDataSource.class);
	}
	
	public ConnectionInfo getConnectionInfo(ConnectionKey key) throws KeeperException, InterruptedException, JsonParseException, JsonMappingException, IOException{
		String result = zooKeeper.getData(PathManager.getConnectionPath(key));
		return CommonUtil.jsonToObject(result, ConnectionInfo.class);
	}
	
	public List<String> getConnectionList(DataSourceKey key){
		try {
			return zooKeeper.getChildren(PathManager.getDataSourcePath(key));
		} catch (KeeperException e) {
			e.printStackTrace();
			return new ArrayList<>();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public void registUUID(String uuid, DataSourceKey key) throws JsonProcessingException, KeeperException, InterruptedException{
		String data = CommonUtil.objectToJson(key);
		zooKeeper.createDataForPersistent(PathManager.getUuidPath(uuid), data);
	}
	
	public void deleteUUID(String uuid) throws InterruptedException, KeeperException{
		zooKeeper.delete(PathManager.getUuidPath(uuid));
	}
	
	public DataSourceKey getDataSourceKey(String uuid) throws KeeperException, InterruptedException, JsonParseException, JsonMappingException, IOException{
		String result = zooKeeper.getData(PathManager.getUuidPath(uuid));
		return CommonUtil.jsonToObject(result, DataSourceKey.class);
	}
}
