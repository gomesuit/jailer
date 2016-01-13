package jailer.web.project;

import jailer.core.model.DataSourceKey;

public class DriverForm extends DataSourceKey{
	private String driverName;
	
	public DriverForm(){}

	public DriverForm(DataSourceKey key) {
		this.setServiceId(key.getServiceId());
		this.setGroupId(key.getGroupId());
		this.setDataSourceId(key.getDataSourceId());
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
}
