package jailer.core.model;

import java.util.Date;
import java.util.Map;

public class ConnectionInfo {
	private String host;
	private String ipAddress;
	private Date sinceConnectTime;
	private String connectUrl;
	private Map<String, String> propertyList;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Date getSinceConnectTime() {
		return sinceConnectTime;
	}
	public void setSinceConnectTime(Date sinceConnectTime) {
		this.sinceConnectTime = sinceConnectTime;
	}
	public String getConnectUrl() {
		return connectUrl;
	}
	public void setConnectUrl(String connectUrl) {
		this.connectUrl = connectUrl;
	}
	public Map<String, String> getPropertyList() {
		return propertyList;
	}
	public void setPropertyList(Map<String, String> propertyList) {
		this.propertyList = propertyList;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
