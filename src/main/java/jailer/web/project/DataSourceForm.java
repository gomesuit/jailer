package jailer.web.project;

import jailer.core.model.DataSourceKey;

public class DataSourceForm extends DataSourceKey{
	private String url;
	private boolean hide;
	
	public DataSourceForm(){}

	public DataSourceForm(DataSourceKey key) {
		this.setServiceId(key.getServiceId());
		this.setGroupId(key.getGroupId());
		this.setDataSourceId(key.getDataSourceId());
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
}
