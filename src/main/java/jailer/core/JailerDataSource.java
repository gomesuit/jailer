package jailer.core;

import java.util.HashMap;
import java.util.Map;

public class JailerDataSource {
	private String dataSourceId;
	private String url;
	private Map<String, String> propertyList = new HashMap<>();
	
	public void addProperty(String key, String value){
		propertyList.put(key, value);
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
}
