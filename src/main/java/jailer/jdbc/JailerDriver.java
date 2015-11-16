package jailer.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class JailerDriver implements Driver{

	private Driver lastUnderlyingDriverRequested;
	
	private String realUrl;
	private Properties info = new Properties();
	
	public JailerDriver(){
		this.realUrl = "jdbc:mysql://localhost/jailer";
		info.setProperty("user", "jailer");
		info.setProperty("password", "password");
	}
	
	static{
		try {
			DriverManager.registerDriver(new JailerDriver());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		url = this.getRealUrl(url);
		Driver d = DriverManager.getDriver(url);
		lastUnderlyingDriverRequested = d;
		info.putAll(this.info);
		return new JailerConnection(d.connect(url, info));
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		url = this.getRealUrl(url);
		Driver d = DriverManager.getDriver(url);
		lastUnderlyingDriverRequested = d;
		return d.acceptsURL(url);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		url = this.getRealUrl(url);
		return lastUnderlyingDriverRequested.getPropertyInfo(url, info);
	}

	@Override
	public int getMajorVersion() {
		return lastUnderlyingDriverRequested.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return lastUnderlyingDriverRequested.getMinorVersion();
	}

	@Override
	public boolean jdbcCompliant() {
		return lastUnderlyingDriverRequested.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return lastUnderlyingDriverRequested.getParentLogger();
	}

	private String getRealUrl(String url)
	{
		return realUrl;
	}

}
