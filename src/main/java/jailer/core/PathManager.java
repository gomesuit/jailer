package jailer.core;

public class PathManager {
	private static final String prefix = "jailer";

	
	public static String getRootPath(){
		return appendPath("", prefix);
	}
	
	public static String getDataSourcePath(String dataSourceId){
		return appendPath(getRootPath(), dataSourceId);
	}
	
	public static String appendPath(String srcPath, String node){
		return srcPath + "/" + node;
	}
}
