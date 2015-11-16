package jailer.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Test4 {

	public static void main(String[] args) throws SQLException {
		MysqlDataSource mysqlDS = new MysqlDataSource();
		mysqlDS.setUrl("jdbc:mysql://localhost/jailer");
		mysqlDS.setUser("jailer");
		mysqlDS.setPassword("password");
		
		Connection conn = mysqlDS.getConnection();

		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT 1 FROM DUAL");

		while (rset.next()) {
			System.out.println(rset.getInt(1));
		}
		
		rset.close();
		stmt.close();
		conn.close();
	}

}
