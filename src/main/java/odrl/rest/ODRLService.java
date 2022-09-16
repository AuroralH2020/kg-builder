package odrl.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import odrl.rest.exception.InternalServiceException;
import spark.Request;

public class ODRLService {

	public static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String ACCEPT_HEADER = "Accept";
	private static final String ACCEPT_HEADER_HTML = "text/html";
	private static final String ACCEPT_HEADER_JSON = "application/json";

	public static boolean shouldReturnHtml(Request request) {
		String accept = request.headers(ACCEPT_HEADER);
		return accept != null && accept.contains(ACCEPT_HEADER_HTML);
	}

	public static boolean shouldReturnJson(Request request) {
		String accept = request.headers(ACCEPT_HEADER);
		return accept != null && accept.contains(ACCEPT_HEADER_JSON);
	}
	
	public static final String toJson(Object o) {
		try {
			return MAPPER.writeValueAsString(o);
		}catch(JsonProcessingException e) {
			throw new InternalServiceException( e.toString());
		}
	}

	public static final Object fromJson(String str, Class<?> clazz) throws IOException {
		try {
			return MAPPER.readValue(str.getBytes(), clazz);
		}catch(JsonProcessingException e) {
			throw new InternalServiceException( e.toString());
		}
	}
	
	public static final String concat(String ... args) {
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg);
		}
		return builder.toString();
	}

	// -- default components methods
	public static String defaultComponentsFile = "./privacy-components.json";
	public static void loadDefaultComponents() {
		
	}

	//TODO
	private static List<Object> readDefaultComponents() {
		try {
			String content = Files.readString(Paths.get(defaultComponentsFile));
			return MAPPER.readValue(content, new TypeReference<List<Object>>(){});
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;

	}

	//TODO
	protected static void registerComponents() {
		
	}
}
