package jailer.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.zookeeper.ZooKeeper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JailerDriver implements Driver{

	private Driver lastUnderlyingDriverRequested;
	
	private String realUrl;
	private Properties info = new Properties();
	
//	public JailerDriver() throws Exception{
//		JailerDataSource JailerDataSource = getJailerDataSource();
//		this.realUrl = JailerDataSource.getUrl();
//		for(JailerProperty jailerProperty : JailerDataSource.getPropertyList()){
//			info.setProperty(jailerProperty.getKey(), jailerProperty.getValue());
//		}
//	}
	
	private JailerDataSource getJailerDataSource(String url) throws Exception{
		ZooKeeper zk = new ZooKeeper("192.168.33.11:2181", 3000, null);
		byte strByte[] = zk.getData("/tmp", false, null);
		String result = new String(strByte, "UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		JailerDataSource jailerDataSource = mapper.readValue(result, JailerDataSource.class);
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
			JailerDataSource JailerDataSource = getJailerDataSource(url);
			this.realUrl = JailerDataSource.getUrl();
			for(JailerProperty jailerProperty : JailerDataSource.getPropertyList()){
				info.setProperty(jailerProperty.getKey(), jailerProperty.getValue());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url = this.getRealUrl(url);
		Driver d = DriverManager.getDriver(url);
		lastUnderlyingDriverRequested = d;
		info.putAll(this.info);
		return new JailerConnection(d.connect(url, info));
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		url = this.getRealUrl(url);
		Driver d = getUnderlyingDriver(url);
		if (d != null) {
			lastUnderlyingDriverRequested = d;
			return true;
		}
		return false;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		url = this.getRealUrl(url);
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

	private String getRealUrl(String url)
	{
		if(realUrl == null){
			JailerDataSource JailerDataSource;
			try {
				JailerDataSource = getJailerDataSource(url);
				this.realUrl = JailerDataSource.getUrl();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return realUrl;
	}
	
	private Driver getUnderlyingDriver(String url) throws SQLException
	{
		url = this.getRealUrl(url);
		Enumeration<Driver> e = DriverManager.getDrivers();

		Driver d;
		while (e.hasMoreElements()) {
			d = e.nextElement();

			if (d.acceptsURL(url)) {
				return d;
			}
		}
		return null;
	}

}
