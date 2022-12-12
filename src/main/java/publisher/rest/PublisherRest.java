package publisher.rest;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.port;
import java.io.IOException;
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
	public static final String CONFIG_PORT_ARGUMENT = "-config.port=";
	public static final String PUBLISHER_PORT_ARGUMENT = "-publisher.port=";
	public static final String DEMON_ARGUMENT = "-d";
	public static Boolean demon = false;
	public static int CONFIG_PORT = 4567;
	public static int PUBLISHER_PORT = 9000;


	public static void main(String[] args) {
		// Configuration
		configure(args);
		// Run services
		runConfigurationMode(CONFIG_PORT);
		runPublishMode(PUBLISHER_PORT);
		// Open browser
		try {
			openBrowsers("http://localhost:"+CONFIG_PORT);
			openBrowsers("http://localhost:"+PUBLISHER_PORT);
			System.out.println("Configuration service running on "+"http://localhost:"+CONFIG_PORT);
			System.out.println("Publisher service running on "+"http://localhost:"+PUBLISHER_PORT);
		}catch(Exception e) {
			e.printStackTrace();
		}
		// Mock data
		StaticPersistence.updateMockData();
	}
	
	public static void openBrowsers(String url) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0) {
			Runtime rt = Runtime.getRuntime();
			rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
		}else if(os.indexOf("mac") >= 0) {
			Runtime rt = Runtime.getRuntime();
			rt.exec("open " + url);
		}else if(os.indexOf("nix") >=0 || os.indexOf("nux") >=0) {
			Runtime rt = Runtime.getRuntime();
			String[] browsers = { "google-chrome", "firefox", "mozilla", "epiphany", "konqueror",
			                                 "netscape", "opera", "links", "lynx" };
			 
			StringBuffer cmd = new StringBuffer();
			for (int i = 0; i < browsers.length; i++)
			    if(i == 0)
			        cmd.append(String.format(    "%s \"%s\"", browsers[i], url));
			    else
			        cmd.append(String.format(" || %s \"%s\"", browsers[i], url)); 
			    // If the first didn't work, try the next browser and so on

			rt.exec(new String[] { "sh", "-c", cmd.toString() });
		}else {
			System.out.println("No valid os was detected");
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void runPublishMode(int port) {
		Service http = Service.ignite().port(port).threadPool(20);
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
	private static void runConfigurationMode(int port) {
		port(port);
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

	private static void configure(String[] args) {
		System.out.println(LOGO);
		// create ViewRenderer
		ViewRendererController.init();
		try {
		for(int index=0; index < args.length; index++) {
			String argument = args[index];
			if(argument!=null && !argument.isBlank()) {
				if(argument.contains(PUBLISHER_PORT_ARGUMENT)) {
					PUBLISHER_PORT = Integer.valueOf(argument.replace(PUBLISHER_PORT_ARGUMENT, ""));
				}else if(argument.contains(CONFIG_PORT_ARGUMENT)) {
					CONFIG_PORT = Integer.valueOf(argument.replace(CONFIG_PORT_ARGUMENT, ""));
				}
				demon = argument.contains(DEMON_ARGUMENT);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
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
