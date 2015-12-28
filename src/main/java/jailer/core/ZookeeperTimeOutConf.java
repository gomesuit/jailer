package jailer.core;

public class ZookeeperTimeOutConf {
	private final int sessionTimeoutMs;
	private final int connectionTimeoutMs;
	
	public ZookeeperTimeOutConf(int sessionTimeoutMs, int connectionTimeoutMs) {
		super();
		this.sessionTimeoutMs = sessionTimeoutMs;
		this.connectionTimeoutMs = connectionTimeoutMs;
	}

	public int getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}

	public int getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}
}
