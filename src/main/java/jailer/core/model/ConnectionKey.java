package jailer.core.model;

public class ConnectionKey extends DataSourceKey{
	private String connectionId;

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
}
