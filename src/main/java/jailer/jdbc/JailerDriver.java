package jailer.jdbc;

import jailer.core.model.ConnectionInfo;
import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.JailerDataSource;
import jailer.core.model.PropertyContents;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.curator.framework.api.CuratorWatcher;

public class JailerDriver implements Driver{

	private Driver lastUnderlyingDriverRequested;
	
	private Properties info = new Properties();
	private JailerDataSource jailerDataSource;
	private URI jailerJdbcURI;
	
	private JdbcRepositoryCurator repository;
	
	public Connection reCreateConnection() throws Exception{
		this.jailerDataSource = getJailerDataSource(jailerJdbcURI);
		info.clear();
		updateInfo(info, jailerDataSource.getPropertyList());
		String realUrl = jailerDataSource.getUrl();
		Driver d = getUnderlyingDriver(realUrl);
//		if (d != null) {
//			lastUnderlyingDriverRequested = d;
//		}
		Connection newConnection = d.connect(realUrl, info);
		lastUnderlyingDriverRequested = d;
		return newConnection;
	}
	
	private void updateInfo(Properties info, Map<String, PropertyContents> propertyList){
		for(Entry<String, PropertyContents> keyValue : propertyList.entrySet()){
			info.put(keyValue.getKey(), keyValue.getValue().getValue());
		}
	}

	public ConnectionKey createConnection(DataSourceKey key) throws Exception{
		ConnectionInfo connectionInfo = createConnectionInfo(jailerDataSource, jailerJdbcURI);
		
		ConnectionKey connectionKey = repository.registConnection(key, connectionInfo);
		System.out.println("createConnection : " + connectionKey.getConnectionId());
		return connectionKey;
	}
	
	private ConnectionInfo createConnectionInfo(JailerDataSource jailerDataSource, URI jailerJdbcURI){
		ConnectionInfo connectionInfo = new ConnectionInfo();
		connectionInfo.setSinceConnectTime(new Date());
		connectionInfo.setConnectUrl(jailerDataSource.getUrl());
		connectionInfo.setHide(jailerDataSource.isHide());
		connectionInfo.setPropertyList(jailerDataSource.getPropertyList());
		connectionInfo.setOptionalParam(JailerJdbcURIManager.getParameterMap(jailerJdbcURI));
		
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			connectionInfo.setHost(inetAddress.getHostName());
			connectionInfo.setIpAddress(inetAddress.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			connectionInfo.setHost("UnknownHost");
			connectionInfo.setIpAddress("UnknownHostAddress");
		}
		
		return connectionInfo;
	}
	
	public void deleteConnection(ConnectionKey key) throws Exception{
		System.out.println("deleteConnection : " + key.getConnectionId());
		repository.deleteConnection(key);
	}
	
	public void dataSourceWatcher(ConnectionKey key, CuratorWatcher watcher) throws Exception{
		repository.watchDataSource(key, watcher);
	}
	
	private JailerDataSource getJailerDataSource(URI uri) throws Exception{
		if(this.jailerJdbcURI == null || !this.jailerJdbcURI.equals(uri)){
			this.jailerJdbcURI = uri;
			String connectString = JailerJdbcURIManager.getConnectString(jailerJdbcURI);
			repository = new JdbcRepositoryCurator(connectString);
		}
		DataSourceKey key = repository.getDataSourceKey(JailerJdbcURIManager.getUUID(jailerJdbcURI));
		JailerDataSource jailerDataSource = repository.getJailerDataSource(key);
		return jailerDataSource;
	}
	
	static{
		try {
			DriverManager.registerDriver(new JailerDriver());
		} catch (SQLException e) {
			throw (RuntimeException) new RuntimeException("could not register jailerjdbc driver!").initCause(e);
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		try {
			if(this.jailerDataSource == null){
				this.jailerDataSource = getJailerDataSource(JailerJdbcURIManager.getUri(url));
			}
			updateInfo(info, jailerDataSource.getPropertyList());
		} catch (Exception e) {
			throw new SQLException(e);
		}
		String realUrl = jailerDataSource.getUrl();
		Driver d = DriverManager.getDriver(realUrl);
		lastUnderlyingDriverRequested = d;
		info.putAll(this.info);
		try {
			DataSourceKey key = repository.getDataSourceKey(JailerJdbcURIManager.getUUID(jailerJdbcURI));
			ConnectionKey connectionKey = createConnection(key);
			System.out.println(realUrl);
			System.out.println(info);
			return new JailerConnection(d.connect(realUrl, info), this, connectionKey);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		try {
			if(this.jailerDataSource == null){
				this.jailerDataSource = getJailerDataSource(JailerJdbcURIManager.getUri(url));
			}
		} catch (Exception e) {
			throw new SQLException(e);
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
				this.jailerDataSource = getJailerDataSource(JailerJdbcURIManager.getUri(url));
			}
		} catch (Exception e) {
			throw new SQLException(e);
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
				this.jailerDataSource = getJailerDataSource(JailerJdbcURIManager.getUri(url));
			}
		} catch (Exception e) {
			throw new SQLException(e);
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
