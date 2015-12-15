package jailer.jdbc.test;

import java.net.URI;

public class UriTest {
	private static final String Prefix = "jdbc:";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String url = "jdbc:jailer://192.168.33.11:2181/prefix?id=aaa&stage=bbb";
		URI uri = new URI(url.substring(Prefix.length()));

		System.out.println(uri.getScheme());
		System.out.println(uri.getHost());
		System.out.println(uri.getPort());
		System.out.println(uri.getPath());
		System.out.println(uri.getQuery());
	}

}
