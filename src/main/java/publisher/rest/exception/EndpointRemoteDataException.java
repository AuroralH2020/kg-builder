package publisher.rest.exception;

import org.apache.http.protocol.HTTP;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

public class EndpointRemoteDataException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3103222182516191834L;

	public EndpointRemoteDataException() {
		super("");
	}


	public EndpointRemoteDataException(String msg) {
		super(msg);
	}

	@SuppressWarnings("rawtypes")
	public static final ExceptionHandler handle = (Exception exception, Request request, Response response) -> {
		response.status(400);
		response.header(HTTP.CONTENT_TYPE, "application/json");
	};

}
