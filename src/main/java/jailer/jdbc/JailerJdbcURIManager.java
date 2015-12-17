package jailer.jdbc;

import jailer.core.PathManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class JailerJdbcURIManager {
	private static final String Prefix = "jdbc:";
	
	private static String getExcludePrefix(String url){
		return url.substring(Prefix.length());
	}

	private static URI getUri(String url) throws URISyntaxException {
		String strUri = getExcludePrefix(url);
		return new URI(strUri);
	}
	
	public static String getHost(String url) throws URISyntaxException {
		return getUri(url).getHost();
	}
	
	public static int getPort(String url) throws URISyntaxException {
		return getUri(url).getPort();
	}
	
	public static String getPath(String url) throws URISyntaxException {
		return PathManager.getRootPath() + getUri(url).getPath();
	}
	
	public static String getUUID(String url) throws URISyntaxException {
		return getUri(url).getPath().substring(1);
	}
	
	public static Map<String, String> getParameterMap(String url) throws URISyntaxException {
		Map<String, String> resultMap = new HashMap<>();
		
		String query = getUri(url).getQuery();
		
		if(query == null){
			return resultMap;
		}
		
		String[] keyValueList = query.split("&");
		for(String keyValue : keyValueList){
			String[] pair = keyValue.split("=");
			resultMap.put(pair[0], pair[1]);
		}
		return resultMap;
	}
}
