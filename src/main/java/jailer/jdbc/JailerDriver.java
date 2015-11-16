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
	
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		url = this.getRealUrl(url);
		Driver d = DriverManager.getDriver(url);
		lastUnderlyingDriverRequested = d;
		return d.connect(url, info);
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
		return "jdbc:mysql://localhost/jailer";
	}

}