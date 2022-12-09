package publisher.rest;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

import java.util.Arrays;

import publisher.rest.controller.AggregatedEndpointController;
import publisher.rest.controller.EndpointController;
import publisher.rest.controller.HtmlViewsController;
import publisher.rest.controller.Management;
import publisher.rest.controller.RouteController;
import publisher.rest.controller.StaticPersistence;
import publisher.rest.controller.ViewRendererController;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.exception.Exceptions;
import publisher.rest.exception.InternalServiceException;
import publisher.rest.exception.InvalidRequestException;
import publisher.rest.exception.RenderTemplateException;
import publisher.rest.exception.RepositoryException;
import spark.Service;

public class PublisherRest {

	// -- Main
	public static boolean CONFIG_MODE = true;


	public static void main(String[] args) {
		Arrays.asList(args).parallelStream().forEach(elem -> System.out.println(elem));
		// Configuration
		configure();

		runConfigurationMode();
		runPublishMode();


		StaticPersistence.updateMockData();

	}
	@SuppressWarnings("unchecked")
	private static void runPublishMode() {
		Service http = Service.ignite().port(9000).threadPool(20);
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
	@SuppressWarnings("unchecked")
	private static void runConfigurationMode() {

		staticFileLocation("");
		get("", Management.endpoints);
		get("/", Management.endpoints);
//		externalStaticFileLocation("./src/main/resources");
		get("/endpoints", Management.endpoints);

		get("/aggregated", Management.aggregated_endpoints);
		get("/routes", Management.routes);
		get("/renderers", Management.renderers);
		get("/templates", Management.templates);

		path("/api", () -> {
			path("/endpoints", () -> {
				get("", EndpointController.list);
				get("/", EndpointController.list);
				get("/:id", EndpointController.get);
				post("", EndpointController.createUpdate);
				post("/:id", EndpointController.createUpdate);
				delete("/:id", EndpointController.remove);
				get("/test/:id", EndpointController.test);
			});
			path("/aggregated", () -> {
				get("", AggregatedEndpointController.list);
				get("/", AggregatedEndpointController.list);
				get("/:id", AggregatedEndpointController.get);
				post("", AggregatedEndpointController.createUpdate);
				post("/:id", AggregatedEndpointController.createUpdate);
				delete("/:id", AggregatedEndpointController.remove);
				get("/test/:id", AggregatedEndpointController.test);
			});
			path("/routes", () -> {
				get("", RouteController.list);
				get("/", RouteController.list);
				get("/:id", RouteController.get);
				post("", RouteController.createUpdate);
				post("/:id", RouteController.createUpdate);
				delete("/:id", RouteController.remove);
			});
			get("/test", RouteController.test);
			path("/views", () -> {
				get("", HtmlViewsController.list);
				get("/", HtmlViewsController.list);
				get("/:id", HtmlViewsController.get);
				post("", HtmlViewsController.update);
				post("/:id", HtmlViewsController.update);
				post("/:id", HtmlViewsController.update);
				delete("/:id", HtmlViewsController.remove);
			});
			path("/renderers", () -> {
				get("", ViewRendererController.list);
				get("/", ViewRendererController.list);
				get("/:id", ViewRendererController.get);
				post("", ViewRendererController.update);
				post("/:id", ViewRendererController.update);
				post("/:id", ViewRendererController.update);
			});
			path("/system", () -> {
				post("", ViewRendererController.updateSystemConfig);
			});
		});

		exception(InternalServiceException.class, InternalServiceException.handle);
		exception(RenderTemplateException.class, RenderTemplateException.handle);
		exception(InvalidRequestException.class, InvalidRequestException.handle);
		exception(RepositoryException.class, RepositoryException.handle);

		// TODO: SETUP RETURN PAYLOADS
		exception(EndpointFormatCompatibilityException.class, EndpointFormatCompatibilityException.handle);
		exception(EndpointRemoteDataException.class, EndpointRemoteDataException.handle);
		exception(Exception.class, Exceptions.handleException);

		after((request, response) -> {
			response.header("Server", "Helio Publisher v4");
			response.header("charset", "utf-8");
			System.out.println(
					"PUBLISHER CONFIG [" + response.status() + "] " + request.requestMethod() + " " + request.url());
		});

	}

	private static void configure() {
		System.out.println(LOGO);
		System.out.println("CONFIG MODE: " + CONFIG_MODE);
		// create ViewRenderer
		ViewRendererController.init();

	}

	private static final String LOGO = "\n" + "  ██╗  ██╗███████╗██╗     ██╗ ██████╗                                 \n"
			+ "  ██║  ██║██╔════╝██║     ██║██╔═══██╗                                \n"
			+ "  ███████║█████╗  ██║     ██║██║   ██║                                \n"
			+ "  ██╔══██║██╔══╝  ██║     ██║██║   ██║                                \n"
			+ "  ██║  ██║███████╗███████╗██║╚██████╔╝                                \n"
			+ "  ╚═╝  ╚═╝╚══════╝╚══════╝╚═╝ ╚═════╝                                 \n"
			+ "  ██████╗ ██╗   ██╗██████╗ ██╗     ██╗███████╗██╗  ██╗███████╗██████╗ \n"
			+ "  ██╔══██╗██║   ██║██╔══██╗██║     ██║██╔════╝██║  ██║██╔════╝██╔══██╗\n"
			+ "  ██████╔╝██║   ██║██████╔╝██║     ██║███████╗███████║█████╗  ██████╔╝\n"
			+ "  ██╔═══╝ ██║   ██║██╔══██╗██║     ██║╚════██║██╔══██║██╔══╝  ██╔══██╗\n"
			+ "  ██║     ╚██████╔╝██████╔╝███████╗██║███████║██║  ██║███████╗██║  ██║\n"
			+ "  ╚═╝      ╚═════╝ ╚═════╝ ╚══════╝╚═╝╚══════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝\n"
			+ "                                                              v0.4.0  \n"
			+ "                                                          \n" + "";

}
