package publisher.rest;


import java.io.IOException;

import publisher.Publisher;
import publisher.rest.controller.AggregatedEndpointController;
import publisher.rest.controller.EndpointController;
import publisher.rest.controller.HtmlViewsController;
import publisher.rest.controller.Management;
import publisher.rest.controller.RouteController;
import publisher.rest.controller.ViewRendererController;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.exception.Exceptions;
import publisher.rest.exception.InternalServiceException;
import publisher.rest.exception.InvalidRequestException;
import publisher.rest.exception.RenderTemplateException;
import publisher.rest.exception.RepositoryException;
import spark.Service;

public class ServiceConfiguration {

	private static Service http;


	public static void stopService() {
		http.stop();
	}

	public static void openInBrowser() {
		try {
			openBrowsers("http://localhost:"+Publisher.CONFIG_PORT);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public
	static void runConfigurationMode(int port) {
		http = Service.ignite().port(port).threadPool(20);
		http.port(port);
		http.staticFileLocation("");
		http.get("", Management.routes);
		http.get("/", Management.routes);
//		externalStaticFileLocation("./src/main/resources");
		http.get("/endpoints", Management.endpoints);

		http.get("/aggregated", Management.aggregated_endpoints);
		http.get("/routes", Management.routes);
		http.get("/renderers", Management.renderers);
		http.get("/templates", Management.templates);

		http.path("/api", () -> {
			http.path("/endpoints", () -> {
				http.get("", EndpointController.list);
				http.get("/", EndpointController.list);
				http.get("/:id", EndpointController.get);
				http.post("", EndpointController.createUpdate);
				http.post("/:id", EndpointController.createUpdate);
				http.delete("/:id", EndpointController.remove);
				http.get("/test/:id", EndpointController.test);
			});
			http.path("/aggregated", () -> {
				http.get("", AggregatedEndpointController.list);
				http.get("/", AggregatedEndpointController.list);
				http.get("/:id", AggregatedEndpointController.get);
				http.post("", AggregatedEndpointController.createUpdate);
				http.post("/:id", AggregatedEndpointController.createUpdate);
				http.delete("/:id", AggregatedEndpointController.remove);
				http.get("/test/:id", AggregatedEndpointController.test);
			});
			http.path("/routes", () -> {
				http.get("", RouteController.list);
				http.get("/", RouteController.list);
				http.get("/:id", RouteController.get);
				http.post("", RouteController.createUpdate);
				http.post("/:id", RouteController.createUpdate);
				http.delete("/:id", RouteController.remove);
			});
			http.get("/test", RouteController.test);
			http.path("/views", () -> {
				http.get("", HtmlViewsController.list);
				http.get("/", HtmlViewsController.list);
				http.get("/:id", HtmlViewsController.get);
				http.post("", HtmlViewsController.update);
				http.post("/:id", HtmlViewsController.update);
				http.post("/:id", HtmlViewsController.update);
				http.delete("/:id", HtmlViewsController.remove);
			});
			http.path("/renderers", () -> {
				http.get("", ViewRendererController.list);
				http.get("/", ViewRendererController.list);
				http.get("/:id", ViewRendererController.get);
				http.post("", ViewRendererController.update);
				http.post("/:id", ViewRendererController.update);
				http.post("/:id", ViewRendererController.update);
			});
			http.path("/system", () -> {
				http.post("", ViewRendererController.updateSystemConfig);
			});
		});

		http.exception(InternalServiceException.class, InternalServiceException.handle);
		http.exception(RenderTemplateException.class, RenderTemplateException.handle);
		http.exception(InvalidRequestException.class, InvalidRequestException.handle);
		http.exception(RepositoryException.class, RepositoryException.handle);

		// TODO: SETUP RETURN PAYLOADS
		http.exception(EndpointFormatCompatibilityException.class, EndpointFormatCompatibilityException.handle);
		http.exception(EndpointRemoteDataException.class, EndpointRemoteDataException.handle);
		http.exception(Exception.class, Exceptions.handleException);

		http.after((request, response) -> {
			response.header("Server", "Helio Publisher v4");
			response.header("charset", "utf-8");
			System.out.println(
					"PUBLISHER CONFIG [" + response.status() + "] " + request.requestMethod() + " " + request.url());
		});

	}

	private static void openBrowsers(String url) throws IOException {
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
}
