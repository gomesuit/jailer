package jailer.web;

import jailer.core.model.DataSourceKey;

public class DataSourceParameterForm extends DataSourceKey{
	private String key;
	private String value;
	
	public DataSourceParameterForm(){}
	
	public DataSourceParameterForm(DataSourceKey key){
		this.setServiceId(key.getServiceId());
		this.setGroupId(key.getGroupId());
		this.setDataSourceId(key.getDataSourceId());
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
