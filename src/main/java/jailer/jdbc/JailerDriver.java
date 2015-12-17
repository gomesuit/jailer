package jailer.jdbc;

import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.JailerDataSource;

import java.net.InetAddress;
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

public class JailerDriver implements Driver{

	private Driver lastUnderlyingDriverRequested;
	
	private Properties info = new Properties();
	private JailerDataSource jailerDataSource;
	private String jailerJdbcURI;
	
	private JdbcRepository repository;
	
	public Connection reCreateConnection() throws Exception{
		this.jailerDataSource = getJailerDataSource(jailerJdbcURI);
		info.clear();
		info.putAll(jailerDataSource.getPropertyList());
		String realUrl = jailerDataSource.getUrl();
		Driver d = DriverManager.getDriver(realUrl);
		lastUnderlyingDriverRequested = d;
		return d.connect(realUrl, info);
	}
	
	public ConnectionKey createConnection(DataSourceKey key) throws Exception{
		InetAddress inetAddress = InetAddress.getLocalHost();
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setHost(inetAddress.getHostName());
		connectionInfo.setIpAddress(inetAddress.getHostAddress());
		connectionInfo.setSinceConnectTime(new Date());
		connectionInfo.setConnectUrl(jailerDataSource.getUrl());
		connectionInfo.setPropertyList(jailerDataSource.getPropertyList());
		
		ConnectionKey connectionKey = repository.registConnection(key, connectionInfo);
		System.out.println("createConnection : " + connectionKey.getConnectionId());
		return connectionKey;
	}
	
	public void deleteConnection(ConnectionKey key) throws Exception{
		System.out.println("deleteConnection : " + key.getConnectionId());
		repository.deleteConnection(key);
	}
	
	public void dataSourceWatcher(DataSourceKey key, Watcher watcher) throws Exception{
		repository.watchDataSource(key, watcher);
	}
	
	private JailerDataSource getJailerDataSource(String url) throws Exception{
		if(this.jailerJdbcURI == null || !this.jailerJdbcURI.equals(url)){
			this.jailerJdbcURI = url;
			String host = JailerJdbcURIManager.getHost(url);
			int port = JailerJdbcURIManager.getPort(url);
			repository = new JdbcRepository(host, port);
		}
		DataSourceKey key = repository.getDataSourceKey(JailerJdbcURIManager.getUUID(url));
		JailerDataSource jailerDataSource = repository.getJailerDataSource(key);
		return jailerDataSource;
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
			DataSourceKey key = repository.getDataSourceKey(JailerJdbcURIManager.getUUID(url));
			ConnectionKey connectionKey = createConnection(key);
			return new JailerConnection(d.connect(realUrl, info), this, connectionKey);
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
