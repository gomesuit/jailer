package jailer.jdbc;

import jailer.core.PathManager;

import java.net.URI;

public class JailerJdbcURIManager {
	private static final String Prefix = "jdbc:";
	
	private static String getExcludePrefix(String url){
		return url.substring(Prefix.length());
	}

	private static URI getUri(String url) throws Exception{
		String strUri = getExcludePrefix(url);
		return new URI(strUri);
	}
	
	public static String getHost(String url) throws Exception{
		return getUri(url).getHost();
	}
	
	public static int getPort(String url) throws Exception{
		return getUri(url).getPort();
	}
	
	public static String getPath(String url) throws Exception{
		return PathManager.getRootPath() + getUri(url).getPath();
	}
	
	public static String getUUID(String url) throws Exception{
		return getUri(url).getPath().substring(1);
	}
}
