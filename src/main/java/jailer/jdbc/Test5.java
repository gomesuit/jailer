package jailer.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class Test5 {

	public static void main(String[] args) throws SQLException {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("jailer.jdbc.JailerDriver");
		ds.setUrl("jdbc:jailer://192.168.33.11:2181/prefix?id=aaa&stage=bbb");
		
		DataSource dataSource = ds;
		
		Connection conn = dataSource.getConnection();

		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT 1 FROM DUAL");

		while (rset.next()) {
			System.out.println(rset.getInt(1));
		}
		
		ds.close();
	}

}
