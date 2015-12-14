package jailer.web.zookeeper;

import jailer.web.DataSourceIdForm;
import jailer.web.JailerDataSource;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ZookeeperService {
	@Autowired
	private ZooKeeper zooKeeper;
	
	private String prefix = "/jailer";
	
	public List<String> getDataSourceIdList() throws Exception{
		return zooKeeper.getChildren(prefix, false);
	}
	
	public void registDataSourceId(DataSourceIdForm form) throws Exception{
		String path = prefix + "/" + form.getDataSourceId();
		JailerDataSource jailerDataSource = new JailerDataSource();
		jailerDataSource.setDataSourceId(form.getDataSourceId());
		String json = objectToJson(jailerDataSource);
		createDataForPersistent(path, json);
	}
	
	private void createDataForPersistent(String path, String data) throws Exception{
		zooKeeper.create(path, data.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	public void registDataSource(JailerDataSource jailerDataSource) throws Exception{
		String path = prefix + "/" + jailerDataSource.getDataSourceId();
		String json = objectToJson(jailerDataSource);
		zooKeeper.setData(path, json.getBytes("UTF-8"), -1);
	}
	
	public JailerDataSource getJailerDataSource(String dataSourceId) throws Exception{
		String path = prefix + "/" + dataSourceId;
		byte[] strByte = zooKeeper.getData(path, false, null);
		String result = new String(strByte, "UTF-8");
		return jsonToObject(result, JailerDataSource.class);
	}
	
	public String objectToJson(Object obj) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	public <T> T jsonToObject(String json, Class<T> clazz) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, clazz);
	}

}
