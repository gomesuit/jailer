package jailer.core;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {

	public static String objectToJson(Object obj) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(obj);
	}
	
	public static <T> T jsonToObject(String json, Class<T> clazz) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, clazz);
	}
}
