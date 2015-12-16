package jailer.core.model;

import java.util.Date;

public class ConnectionInfo {
	private String host;
	private Date sinceConnectTime;
	
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
}
