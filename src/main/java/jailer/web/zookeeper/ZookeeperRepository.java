package jailer.web.zookeeper;

import java.util.ArrayList;
import java.util.List;

import jailer.core.CommonUtil;
import jailer.core.JailerZookeeperCurator;
import jailer.core.PathManager;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ZookeeperRepository {
	@Autowired
	private JailerZookeeperCurator zooKeeper;
	
	public List<String> getServiceList() {
		try {
			return zooKeeper.getChildren(PathManager.getDataSourceRootPath());
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public String getConnectString() {
		return zooKeeper.getConnectString();
	}

	public void registGroup(GroupKey key) throws Exception{
		zooKeeper.createDataForPersistent(PathManager.getGroupPath(key), "");
	}

	public void deleteGroup(GroupKey key) throws Exception{
		zooKeeper.delete(PathManager.getGroupPath(key));
	}

	public List<String> getGroupList(ServiceKey key){
		try {
			return zooKeeper.getChildren(PathManager.getServicePath(key));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void registDataSource(DataSourceKey key, JailerDataSource dataSource) throws Exception{
		String data = CommonUtil.objectToJson(dataSource);
		zooKeeper.createDataForPersistent(PathManager.getDataSourcePath(key), data);
	}

	public void deleteDataSource(DataSourceKey key) throws Exception{
		zooKeeper.delete(PathManager.getDataSourcePath(key));
	}

	public List<String> getDataSourceList(GroupKey key){
		try {
			return zooKeeper.getChildren(PathManager.getGroupPath(key));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public void updateDataSource(DataSourceKey key, JailerDataSource jailerDataSource) throws Exception {
		String data = CommonUtil.objectToJson(jailerDataSource);
		zooKeeper.setData(PathManager.getDataSourcePath(key), data);
	}

	public JailerDataSource getDataSource(DataSourceKey key) throws Exception {
		String result = zooKeeper.getData(PathManager.getDataSourcePath(key));
		return CommonUtil.jsonToObject(result, JailerDataSource.class);
	}
	
	public ConnectionInfo getConnectionInfo(ConnectionKey key) throws Exception{
		String result = zooKeeper.getData(PathManager.getConnectionPath(key));
		return CommonUtil.jsonToObject(result, ConnectionInfo.class);
	}
	
	public List<String> getConnectionList(DataSourceKey key){
		try {
			return zooKeeper.getChildren(PathManager.getDataSourcePath(key));
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public void registUUID(String uuid, DataSourceKey key) throws Exception{
		String data = CommonUtil.objectToJson(key);
		zooKeeper.createDataForPersistent(PathManager.getUuidPath(uuid), data);
	}
	
	public void deleteUUID(String uuid) throws Exception{
		zooKeeper.delete(PathManager.getUuidPath(uuid));
	}
	
	public DataSourceKey getDataSourceKey(String uuid) throws Exception{
		String result = zooKeeper.getData(PathManager.getUuidPath(uuid));
		return CommonUtil.jsonToObject(result, DataSourceKey.class);
	}
}
