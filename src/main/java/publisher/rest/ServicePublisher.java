package publisher.rest;

import publisher.rest.controller.RouteController;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.exception.Exceptions;
import publisher.rest.exception.InternalServiceException;
import publisher.rest.exception.InvalidRequestException;
import publisher.rest.exception.RenderTemplateException;
import publisher.rest.exception.RepositoryException;
import spark.Service;

public class ServicePublisher {

	private static Service http;


	public static void stopService() {
		http.stop();
	}

	@SuppressWarnings("unchecked")
	public
	static void runPublishMode(int port) {
		http = Service.ignite().port(port).threadPool(20);
		http.get("/*/", RouteController.publish);
		http.get("/*", RouteController.publish);
		http.get("*", RouteController.publish);

		http.after((request, response) -> {
			response.header("Server", "Helio Publisher v4");
			response.header("charset", "utf-8");
			System.out
					.println("PUBLISHER [" + response.status() + "] " + request.requestMethod() + " " + request.url());
		});

		http.exception(InternalServiceException.class, InternalServiceException.handle);
		http.exception(RenderTemplateException.class, RenderTemplateException.handle);
		http.exception(InvalidRequestException.class, InvalidRequestException.handle);
		http.exception(RepositoryException.class, RepositoryException.handle);

	    // TODO: SETUP RETURN PAYLOADS
		http.exception(EndpointFormatCompatibilityException.class, EndpointFormatCompatibilityException.handle);
		http.exception(EndpointRemoteDataException.class, EndpointRemoteDataException.handle);
		http.exception(Exception.class, Exceptions.handleException);

	}
}
