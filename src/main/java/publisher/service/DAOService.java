package publisher.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import publisher.rest.exception.InternalServiceException;
import spark.Request;

public class DAOService {

	public static final ObjectMapper MAPPER = new ObjectMapper();
	public static final Gson GSON = new Gson();

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

	public static final JsonObject castToJson(String json) {
		return GSON.fromJson(json, JsonObject.class);
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
			return MAPPER.readValue(str, clazz);
		}catch(JsonProcessingException e) {
			throw new InternalServiceException( e.getMessage());
		}
	}

	public static final String concat(String ... args) {
		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg);
		}
		return builder.toString();
	}


}
