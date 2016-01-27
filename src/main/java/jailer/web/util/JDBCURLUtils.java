package jailer.web.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.thymeleaf.util.StringUtils;

public class JDBCURLUtils {
	private static final String Prefix = "jdbc:";
	
	public static String getExcludePrefix(String url){
		return "//" + url.split("//")[1];
	}
	
	public static boolean isJDBCURL(String url){
		if(StringUtils.isEmptyOrWhitespace(url)) return false;
		
		if(!url.startsWith(Prefix)){
			return false;
		}
		
		String jdbcUrl = getExcludePrefix(url);
		
		URI uri = null;
		try {
			uri = new URI(jdbcUrl);
		} catch (URISyntaxException e) {
			return false;
		}
		
		if(uri.getHost() == null){
			return false;
		}
		
		return true;
	}
	
	public static String getHost(String url){
		if(!isJDBCURL(url)) return null;
		
		String jdbcUrl = getExcludePrefix(url);
		
		URI uri = null;
		try {
			uri = new URI(jdbcUrl);
		} catch (URISyntaxException e) {
			return null;
		}
		
		return uri.getHost();
	}
	
	public static String getDatabaseName(String url){
		if(!isJDBCURL(url)) return null;
		
		String jdbcUrl = getExcludePrefix(url);
		
		URI uri = null;
		try {
			uri = new URI(jdbcUrl);
		} catch (URISyntaxException e) {
			return null;
		}
		
		return uri.getPath().split("/")[1];
	}
}
