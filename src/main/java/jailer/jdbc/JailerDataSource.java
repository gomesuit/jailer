package jailer.jdbc;

import java.util.ArrayList;
import java.util.List;

public class JailerDataSource {
	private String url;
	private List<JailerProperty> propertyList = new ArrayList<>();
	
	public void addProperty(String key, String value){
		propertyList.add(new JailerProperty(key, value)); 
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<JailerProperty> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(List<JailerProperty> propertyList) {
		this.propertyList = propertyList;
	}

	@Override
	public String toString() {
		return "JailerDataSource [url=" + url + ", propertyList="
				+ propertyList + "]";
	}
}
