package jailer.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Test3 {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("jailer.jdbc.JailerDriver");
		Connection conn = DriverManager.getConnection("ajdbc:log4jdbc:mysql://localhost/jailer", "jailer", "password");
		
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
