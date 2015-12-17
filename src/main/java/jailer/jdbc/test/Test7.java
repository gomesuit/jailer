package jailer.jdbc.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class Test7 {

	public static void main(String[] args) throws SQLException {
		BasicDataSource ds1 = new BasicDataSource();
		ds1.setDriverClassName("jailer.jdbc.JailerDriver");
		ds1.setUrl("jdbc:jailer://192.168.33.11:2181/69580f5d-3388-475b-a52d-7a6259057c67?project=testproject");
		ds1.setInitialSize(5);
		DataSource dataSource1 = ds1;
		Connection conn1 = dataSource1.getConnection();

		BasicDataSource ds2 = new BasicDataSource();
		ds2.setDriverClassName("jailer.jdbc.JailerDriver");
		ds2.setUrl("jdbc:jailer://192.168.33.11:2181/b11292ba-2664-4b34-a37f-0dfdb298b8e5?project=testproject");
		ds2.setInitialSize(5);
		DataSource dataSource2 = ds2;
		Connection conn2 = dataSource2.getConnection();
		
		boolean flg = true;
		while(flg){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 1111111111111111111111111
			Statement stmt1 = conn1.createStatement();
			ResultSet rset1 = stmt1.executeQuery("SELECT id FROM test");

			while (rset1.next()) {
				System.out.println(rset1.getInt(1));
			}
			rset1.close();
			stmt1.close();
			

			// 2222222222222222222222222
			Statement stmt2 = conn2.createStatement();
			ResultSet rset2 = stmt2.executeQuery("SELECT id FROM test");

			while (rset2.next()) {
				System.out.println(rset2.getInt(1));
			}
			rset2.close();
			stmt2.close();
			
		}
		conn1.close();
		ds1.close();

		conn2.close();
		ds2.close();
	}

}
