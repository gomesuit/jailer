package jailer.web.project;

import jailer.core.model.PropertyContents;

import java.util.Date;
import java.util.Map;

public class ConnectionSearchInfoRow {
	private String group;
	private String id;
	private String connectionId;
	private String host;
	private String ipAddress;
	private Date sinceConnectTime;
	private String driverName;
	private String connectUrl;
	private boolean hide;
	private Map<String, PropertyContents> propertyList;
	private Map<String, String> optionalParam;
	private boolean warning = false;
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getSinceConnectTime() {
		return sinceConnectTime;
	}
	public void setSinceConnectTime(Date sinceConnectTime) {
		this.sinceConnectTime = sinceConnectTime;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getConnectUrl() {
		return connectUrl;
	}
	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
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
	public Map<String, String> getOptionalParam() {
		return optionalParam;
	}
	public void setOptionalParam(Map<String, String> optionalParam) {
		this.optionalParam = optionalParam;
	}
	public boolean isWarning() {
		return warning;
	}
	public void setWarning(boolean warning) {
		this.warning = warning;
	}
	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
