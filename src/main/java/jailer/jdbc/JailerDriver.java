package jailer.jdbc;

import jailer.core.CommonUtil;
import jailer.core.JansibleZookeeper;
import jailer.core.model.ConnectionInfo;
import jailer.core.model.JailerDataSource;

import java.net.InetAddress;
import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.zookeeper.Watcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JailerDriver implements Driver{
	private static final String Prefix = "jdbc:";
	private static final String zookeeperRoot = "/jailer";

	private Driver lastUnderlyingDriverRequested;
	
	private Properties info = new Properties();
	private JailerDataSource jailerDataSource;
	private JansibleZookeeper zooKeeper;
	private String url;
	
	public Connection reCreateConnection(String path) throws Exception{
		this.jailerDataSource = getJailerDataSource(url);
		info.clear();
		info.putAll(jailerDataSource.getPropertyList());
		String realUrl = jailerDataSource.getUrl();
		Driver d = DriverManager.getDriver(realUrl);
		lastUnderlyingDriverRequested = d;
		return d.connect(realUrl, info);
	}
	
	public String createConnection(String path) throws Exception{
		InetAddress inetAddress = InetAddress.getLocalHost();
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setHost(inetAddress.getHostName());
		connectionInfo.setIpAddress(inetAddress.getHostAddress());
		connectionInfo.setSinceConnectTime(new Date());
		connectionInfo.setConnectUrl(jailerDataSource.getUrl());
		connectionInfo.setPropertyList(jailerDataSource.getPropertyList());
		
		String data = CommonUtil.objectToJson(connectionInfo);
		
		String connectionPath = zooKeeper.createDataForEphemeralSequential(path + "/", data);
		System.out.println("createConnection : " + connectionPath);
		return connectionPath;
	}
	
	public void deleteConnection(String path) throws Exception{
		System.out.println("deleteConnection : " + path);
		zooKeeper.delete(path);
	}
	
	public void dataSourceWatcher(Watcher watcher) throws Exception{
		zooKeeper.exists(getPath(url), watcher);
	}
	
	private JailerDataSource getJailerDataSource(String url) throws Exception{
		if(this.url == null || !this.url.equals(url)){
			this.url = url;
			String host = getHost(url);
			int port = getPort(url);
			zooKeeper = new JansibleZookeeper(host, port);
		}
		String path = getPath(url);
		String result = zooKeeper.getData(path);
		ObjectMapper mapper = new ObjectMapper();
		JailerDataSource jailerDataSource = mapper.readValue(result, JailerDataSource.class);
		return jailerDataSource;
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
			info.putAll(jailerDataSource.getPropertyList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String realUrl = jailerDataSource.getUrl();
		Driver d = DriverManager.getDriver(realUrl);
		lastUnderlyingDriverRequested = d;
		info.putAll(this.info);
		try {
			String connectionPath = createConnection(getPath(url));
			return new JailerConnection(d.connect(realUrl, info), this, connectionPath);
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
