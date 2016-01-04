package jailer.jdbc.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class Test6 {

	public static void main(String[] args) throws SQLException {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("jailer.jdbc.JailerDriver");
		ds.setUrl("jdbc:jailer://192.168.33.11:2181/e5ed5a1d-3b22-4b51-806c-024bd1f8b61f?project=testproject");
		ds.setInitialSize(10);
		
		DataSource dataSource = ds;
		Connection conn = dataSource.getConnection();
		
		boolean flg = true;
		while(flg){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT id FROM test");

			while (rset.next()) {
				System.out.println(rset.getInt(1));
			}
			rset.close();
			stmt.close();
		}
		conn.close();
		ds.close();
	}

}
