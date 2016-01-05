package jailer.core.model;

import java.util.HashMap;
import java.util.Map;

public class JailerDataSource {
	private String dataSourceId;
	private String url;
	private boolean hide;
	private Map<String, PropertyContents> propertyList = new HashMap<>();
	private String uuid;
	
	public void addProperty(String key, PropertyContents contents){
		propertyList.put(key, contents);
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

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public Map<String, PropertyContents> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(Map<String, PropertyContents> propertyList) {
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
