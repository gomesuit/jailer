package jailer.web.project;

import jailer.core.CommonUtil;
import jailer.core.encrypt.JailerEncryptException;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;
import jailer.web.util.JDBCURLUtils;
import jailer.web.zookeeper.ZookeeperRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JailerService {
	@Autowired
	private ZookeeperRepository repository;
	
	public List<String> getDataSourceIdList(GroupKey key){
		return repository.getDataSourceList(key);
	}
	
	public String getConnectString() {
		return repository.getConnectString();
	}
	
	public List<RowConnectionInfo> getConnectionInfoList(ServiceKey key) throws Exception{
		List<RowConnectionInfo> connectionInfoList = new ArrayList<>();
		
		for(String group : getGroupList(key)){
			GroupKey groupKey = new GroupKey();
			groupKey.setServiceId(key.getServiceId());
			groupKey.setGroupId(group);
			for(String dataSourceId : getDataSourceIdList(groupKey)){
				DataSourceKey dataSourceKey = new DataSourceKey();
				dataSourceKey.setServiceId(groupKey.getServiceId());
				dataSourceKey.setGroupId(groupKey.getGroupId());
				dataSourceKey.setDataSourceId(dataSourceId);
				
				RowConnectionInfo connectionInfo = createRowConnectionInfo(dataSourceKey);
				connectionInfoList.add(connectionInfo);
			}
		}
		
		return connectionInfoList;
	}
	
	private RowConnectionInfo createRowConnectionInfo(DataSourceKey key) throws Exception{
		RowConnectionInfo connectionInfo = new RowConnectionInfo();
		connectionInfo.setGroup(key.getGroupId());
		connectionInfo.setId(key.getDataSourceId());
		try{
			connectionInfo.setPoint(repository.getConnectionNum(key));
		}catch(KeeperException e){
			connectionInfo.setPoint(99999);
		}
		try{
			connectionInfo.setUuid(repository.getDataSource(key).getUuid());
		}catch(JailerEncryptException e){
			connectionInfo.setUuid("Fail in decrypt!!");
		}catch(KeeperException e){
			connectionInfo.setUuid("KeeperException!!");
		}
		return connectionInfo;
	}
	
	public void registDataSourceId(DataSourceKey key) throws Exception{
		String uuid = CommonUtil.getRandomUUID();
		
		JailerDataSource jailerDataSource = new JailerDataSource();
		jailerDataSource.setDataSourceId(key.getDataSourceId());
		jailerDataSource.setUuid(uuid);
		repository.registDataSource(key, jailerDataSource);
		
		try {
			repository.registUUID(uuid, key);
		} catch (Exception e) {
			e.printStackTrace();
			repository.deleteDataSource(key);
		}
	}
	
	public void deleteDataSourceId(DataSourceKey key) throws Exception{
		repository.deleteDataSource(key);
	}
	
	public void registDataSourcePlan(DataSourceKey key, JailerDataSource jailerDataSource) throws Exception{
		repository.updateDataSourcePlan(key, jailerDataSource);
	}
	
	public void registDataSourceCorrent(DataSourceKey key, JailerDataSource jailerDataSource) throws Exception{
		repository.updateDataSourceCorrent(key, jailerDataSource);
	}
	
	public JailerDataSource getJailerDataSource(DataSourceKey key) throws Exception{
		return repository.getDataSource(key);
	}
	
	public JailerDataSource getJailerDataSourcePlan(DataSourceKey key) throws Exception {
		return repository.getDataSourcePlan(key);
	}
	
	public boolean isExistCheck(DataSourceKey key, String url) throws Exception{

		for(String group : getGroupList(key)){
			GroupKey groupKey = new GroupKey();
			groupKey.setServiceId(key.getServiceId());
			groupKey.setGroupId(group);
			for(String dataSourceId : getDataSourceIdList(groupKey)){
				DataSourceKey dataSourceKey = new DataSourceKey();
				dataSourceKey.setServiceId(groupKey.getServiceId());
				dataSourceKey.setGroupId(groupKey.getGroupId());
				dataSourceKey.setDataSourceId(dataSourceId);
				
				if(key.equals(dataSourceKey)) continue;
				
				String registedUrl = repository.getDataSource(dataSourceKey).getUrl();
				if(isSame(url, registedUrl)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean isSame(String urlA, String urlB){
		try{
			if(!JDBCURLUtils.isJDBCURL(urlA)) return false;
			if(!JDBCURLUtils.isJDBCURL(urlB)) return false;
			
			URI uriA = new URI(JDBCURLUtils.getExcludePrefix(urlA));
			URI uriB = new URI(JDBCURLUtils.getExcludePrefix(urlB));
			
			if(uriA.equals(uriB)){
				return true;
			}

			if(!uriA.getHost().equals(uriB.getHost())){
				return false;
			}

			if(!uriA.getPath().equals(uriB.getPath())){
				return false;
			}
			
			return true;
		}catch(Exception e){
			return false;
		}
	}

	public Map<String, ConnectionInfo> getConnectionList(DataSourceKey key) throws Exception{
		Map<String, ConnectionInfo> connectionList = new LinkedHashMap<>();
		
		boolean success = false;
		
		while(!success){		
			try{
				for(String connectionId : repository.getConnectionList(key)){
					ConnectionKey connectionKey = new ConnectionKey();
					connectionKey.setServiceId(key.getServiceId());
					connectionKey.setGroupId(key.getGroupId());
					connectionKey.setDataSourceId(key.getDataSourceId());
					connectionKey.setConnectionId(connectionId);
					
					connectionList.put(connectionId, repository.getConnectionInfo(connectionKey));
				}
				success = true;
			}catch(KeeperException e){
				e.printStackTrace();
				connectionList.clear();
			}
		}
		
		return connectionList;
	}

	
	public void registService(ServiceKey key) throws Exception{
		repository.registService(key);
	}
	
	public void deleteService(ServiceKey key) throws Exception{
		repository.deleteService(key);
	}
	
	public void registGroup(GroupKey key) throws Exception{
		repository.registGroup(key);
	}
	
	public void deleteGroup(GroupKey key) throws Exception{
		repository.deleteGroup(key);
	}
	
	public List<String> getGroupList(ServiceKey key){
		return repository.getGroupList(key);
	}

	public List<String> getServiceList() {
		return repository.getServiceList();
	}

}
