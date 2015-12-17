package jailer.core;

import jailer.core.model.ConnectionKey;
import jailer.core.model.DataSourceKey;
import jailer.core.model.GroupKey;
import jailer.core.model.ServiceKey;

public class PathManager {
	private static final String prefix = "jailer";
	
	public static String getRootPath(){
		return appendPath("", prefix);
	}
	
	public static String getDataSourceRootPath(){
		return appendPath(getRootPath(), "DataSource");
	}
	
	public static String getUrlManagerPath(){
		return appendPath(getRootPath(), "UrlManager");
	}
	
	public static String getServicePath(ServiceKey key){
		return appendPath(getDataSourceRootPath(), key.getServiceId());
	}
	
	public static String getGroupPath(GroupKey key){
		return appendPath(getServicePath(key), key.getGroupId());
	}
	
	public static String getDataSourcePath(DataSourceKey key){
		return appendPath(getGroupPath(key), key.getDataSourceId());
	}
	
	public static String getDataSourcePath(String dataSourceId){
		return appendPath(getRootPath(), dataSourceId);
	}
	
	private static String appendPath(String srcPath, String node){
		return srcPath + "/" + node;
	}
	
	public static String getConnectionPath(String dataSourceId, String connectionId){
		return appendPath(getDataSourcePath(dataSourceId), connectionId);
	}
	
	public static String getConnectionPath(ConnectionKey key){
		return appendPath(getDataSourcePath(key), key.getConnectionId());
	}
}
