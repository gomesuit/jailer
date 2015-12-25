package jailer.core.model;

public class ConnectionKey extends DataSourceKey{
	private String connectionId;

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((connectionId == null) ? 0 : connectionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionKey other = (ConnectionKey) obj;
		if (connectionId == null) {
			if (other.connectionId != null)
				return false;
		} else if (!connectionId.equals(other.connectionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionKey [connectionId=" + connectionId + "]";
	}
}
