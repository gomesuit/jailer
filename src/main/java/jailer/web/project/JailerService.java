package jailer.web.project;

import jailer.core.CommonUtil;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.ServiceKey;
import jailer.web.zookeeper.ZookeeperRepository;

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
				
				RowConnectionInfo connectionInfo = new RowConnectionInfo();
				connectionInfo.setGroup(group);
				connectionInfo.setId(dataSourceId);
				connectionInfo.setPoint(repository.getConnectionNum(dataSourceKey));
				connectionInfo.setUuid(repository.getDataSource(dataSourceKey).getUuid());
				connectionInfoList.add(connectionInfo);
			}
		}
		
		return connectionInfoList;
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
	
	public void registDataSource(DataSourceKey key, JailerDataSource jailerDataSource) throws Exception{
		repository.updateDataSource(key, jailerDataSource);
	}
	
	public JailerDataSource getJailerDataSource(DataSourceKey key) throws Exception{
		return repository.getDataSource(key);
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
