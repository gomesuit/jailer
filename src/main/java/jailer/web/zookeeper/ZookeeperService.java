package jailer.web.zookeeper;

import jailer.core.CommonUtil;
import jailer.core.JansibleZookeeper;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.JailerDataSource;
import jailer.web.DataSourceIdForm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZookeeperService {
	@Autowired
	private JansibleZookeeper zooKeeper;
	
	private String prefix = "jailer";
	
	public List<String> getDataSourceIdList() throws Exception{
		return zooKeeper.getChildren(getRootPath());
	}
	
	public void registDataSourceId(DataSourceIdForm form) throws Exception{
		String path = getDataSourcePath(form.getDataSourceId());
		JailerDataSource jailerDataSource = new JailerDataSource();
		jailerDataSource.setDataSourceId(form.getDataSourceId());
		String json = CommonUtil.objectToJson(jailerDataSource);
		zooKeeper.createDataForPersistent(path, json);
	}
	
	public void registDataSource(JailerDataSource jailerDataSource) throws Exception{
		String path = getDataSourcePath(jailerDataSource.getDataSourceId());
		String json = CommonUtil.objectToJson(jailerDataSource);
		zooKeeper.setData(path, json);
	}
	
	public JailerDataSource getJailerDataSource(String dataSourceId) throws Exception{
		String path = getDataSourcePath(dataSourceId);
		String result = zooKeeper.getData(path);
		return CommonUtil.jsonToObject(result, JailerDataSource.class);
	}
	
	public Map<String, ConnectionInfo> getConnectionList(String dataSourceId) throws Exception{
		Map<String, ConnectionInfo> connectionList = new LinkedHashMap<>();
		String dataSourcePath = getDataSourcePath(dataSourceId);
		
		boolean success = false;
		
		while(!success){		
			try{
				for(String connectionId : zooKeeper.getChildren(dataSourcePath)){
					String connectionPath = appendPath(dataSourcePath, connectionId);
					String result = zooKeeper.getData(connectionPath);
					connectionList.put(connectionId, CommonUtil.jsonToObject(result, ConnectionInfo.class));
				}
				success = true;
			}catch(KeeperException e){
				e.printStackTrace();
				connectionList.clear();
			}
		}
		
		return connectionList;
	}
	
	private String getRootPath(){
		return "/" + prefix;
	}
	
	private String getDataSourcePath(String dataSourceId){
		return appendPath(getRootPath(), dataSourceId);
	}
	
	private String appendPath(String srcPath, String node){
		return srcPath + "/" + node;
	}

}
