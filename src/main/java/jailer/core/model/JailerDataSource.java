package jailer.core.model;

import java.util.HashMap;
import java.util.Map;

public class JailerDataSource {
	private String dataSourceId;
	private String url;
	private Map<String, String> propertyList = new HashMap<>();
	private String uuid;
	
	public void addProperty(String key, String value){
		propertyList.put(key, value);
	}

	public void removeProperty(String key){
		propertyList.remove(key);
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(Map<String, String> propertyList) {
		this.propertyList = propertyList;
	}

	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
