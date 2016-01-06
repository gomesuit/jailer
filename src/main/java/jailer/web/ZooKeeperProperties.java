package jailer.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zookeeper")
public class ZooKeeperProperties {
	private String connectionString;

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

}
