package jailer.jdbc.test;

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
		ds.setUrl("jdbc:jailer://192.168.33.11:2181/test?id=aaa&stage=bbb");
		ds.setInitialSize(5);
		
		DataSource dataSource = ds;
		Connection conn = dataSource.getConnection();
		
		boolean flg = true;
		while(flg){
			try {
				Thread.sleep(1000);
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
		
		ds.close();
	}

}
