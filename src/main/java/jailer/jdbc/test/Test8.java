package jailer.jdbc.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public class Test8 {

	public static void main(String[] args) throws SQLException {
		ExecutorService exec = Executors.newFixedThreadPool(3);
		
		exec.execute(new Runnable1());
		exec.execute(new Runnable2());
		
		
	}
	
	private static class Runnable1 implements Runnable{

		@Override
		public void run() {
			try{
				
				BasicDataSource ds = new BasicDataSource();
				ds.setDriverClassName("jailer.jdbc.JailerDriver");
				ds.setUrl("jdbc:jailer://192.168.33.11:2181/1edd6cfd-0824-4cab-bda3-1f5babfbfbc3?project=testproject");
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
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	private static class Runnable2 implements Runnable{

		@Override
		public void run() {
			try{
				
				BasicDataSource ds = new BasicDataSource();
				ds.setDriverClassName("jailer.jdbc.JailerDriver");
				ds.setUrl("jdbc:jailer://192.168.33.11:2181/60acba3e-739c-4049-88fb-46e9dd61a0d2?project=testproject");
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
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

}
