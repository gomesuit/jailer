package jailer.jdbc;

import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JailerDriver implements Driver{
	private static final String Prefix = "jdbc:";
	private static final String zookeeperRoot = "/jailer";

	private Driver lastUnderlyingDriverRequested;
	
	private Properties info = new Properties();
	private JailerDataSource jailerDataSource;
	private ZooKeeper zooKeeper;
	private String url;
	
	public Connection reCreateConnection(String path, Watcher watcher) throws Exception{
		this.jailerDataSource = getJailerDataSource(url);
		for(JailerProperty jailerProperty : jailerDataSource.getPropertyList()){
			info.setProperty(jailerProperty.getKey(), jailerProperty.getValue());
		}
		String realUrl = jailerDataSource.getUrl();
		Driver d = DriverManager.getDriver(realUrl);
		lastUnderlyingDriverRequested = d;
		zooKeeper.exists(path, watcher);
		createConnection(path);
		return d.connect(realUrl, info);
	}
	
	private void createConnection(String path) throws Exception{
		zooKeeper.create(path + "/", "test".getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	public void dataSourceWatcher(Watcher watcher) throws Exception{
		zooKeeper.exists(getPath(url), watcher);
	}
	
	private JailerDataSource getJailerDataSource(String url) throws Exception{
		if(this.url == null || !this.url.equals(url)){
			this.url = url;
			String host = getHost(url);
			int port = getPort(url);
			zooKeeper = new ZooKeeper(host + ":" + port, 3000, new DefaultWatcher());
		}
		String path = getPath(url);
		byte strByte[] = zooKeeper.getData(path, null, null);
		String result = new String(strByte, "UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		JailerDataSource jailerDataSource = mapper.readValue(result, JailerDataSource.class);
		return jailerDataSource;
	}
	
	private class DefaultWatcher implements Watcher{

		@Override
		public void process(WatchedEvent event) {
			System.out.println("DefaultWatcher.process!");
		}
		
	}
	
	private String getPath(String url) throws Exception{
		return zookeeperRoot + getUri(url).getPath();
	}
	
	private String getHost(String url) throws Exception{
		return getUri(url).getHost();
	}
	
	private int getPort(String url) throws Exception{
		return getUri(url).getPort();
	}
	
	private String getExcludePrefix(String url){
		return url.substring(Prefix.length());
	}
	
	private URI getUri(String url) throws Exception{
		String strUri = getExcludePrefix(url);
		return new URI(strUri);
	}
	
	static{
		try {
			DriverManager.registerDriver(new JailerDriver());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		try {
			if(this.jailerDataSource == null){
				this.jailerDataSource = getJailerDataSource(url);
			}
			for(JailerProperty jailerProperty : jailerDataSource.getPropertyList()){
				info.setProperty(jailerProperty.getKey(), jailerProperty.getValue());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String realUrl = jailerDataSource.getUrl();
		Driver d = DriverManager.getDriver(realUrl);
		lastUnderlyingDriverRequested = d;
		info.putAll(this.info);
		try {
			createConnection(getPath(url));
			return new JailerConnection(d.connect(realUrl, info), this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SQLException();
		}
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		try {
			if(this.jailerDataSource == null){
				this.jailerDataSource = getJailerDataSource(url);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String realUrl = jailerDataSource.getUrl();
		Driver d = getUnderlyingDriver(realUrl);
		if (d != null) {
			lastUnderlyingDriverRequested = d;
			return true;
		}
		return false;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		try {
			if(this.jailerDataSource == null){
				this.jailerDataSource = getJailerDataSource(url);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastUnderlyingDriverRequested.getPropertyInfo(url, info);
	}

	@Override
	public int getMajorVersion() {
		if (lastUnderlyingDriverRequested == null) {
			return 1;
		} 
		return lastUnderlyingDriverRequested.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		if (lastUnderlyingDriverRequested == null) {
			return 0;
		}
		return lastUnderlyingDriverRequested.getMinorVersion();
	}

	@Override
	public boolean jdbcCompliant() {
		return lastUnderlyingDriverRequested != null &&
				   lastUnderlyingDriverRequested.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return lastUnderlyingDriverRequested.getParentLogger();
	}
	
	private Driver getUnderlyingDriver(String url) throws SQLException{
		try {
			if(this.jailerDataSource == null){
				this.jailerDataSource = getJailerDataSource(url);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String realUrl = jailerDataSource.getUrl();
		Enumeration<Driver> e = DriverManager.getDrivers();

		Driver d;
		while (e.hasMoreElements()) {
			d = e.nextElement();

			if (d.acceptsURL(realUrl)) {
				return d;
			}
		}
		return null;
	}

}
