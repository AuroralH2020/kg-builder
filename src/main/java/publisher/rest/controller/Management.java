package publisher.rest.controller;

import java.util.Map;

import publisher.rest.model.renderers.FreemarkerRenderer;
import publisher.rest.model.renderers.ViewRenderer;
import spark.Request;
import spark.Response;
import spark.Route;

public class Management {

	private static ViewRenderer render = new FreemarkerRenderer("./src/main/resources/");

	public static final Route endpoints = (Request request, Response response) -> {
		response.type("text/html");
		Map<String,Object> model = render.toModel(EndpointController.service.getAll(), "endpoints");
		return render.render(model, "endpoints.ftl");
	};

	public static final Route aggregated_endpoints = (Request request, Response response) -> {
		response.type("text/html");
		Map<String,Object> model = render.toModel(AggregatedEndpointController.service.getAll(), "aggregated");
		Map<String,Object> model2 = render.toModel(EndpointController.service.getAll(), "endpoints");
		model.putAll(model2);
		return render.render(model, "aggregated.ftl");
	};

	public static final Route routes = (Request request, Response response) -> {
		response.type("text/html");
		Map<String,Object> model = render.toModel(RouteController.service.getAll(), "routes");
		Map<String,Object> model2 = render.toModel(EndpointController.service.getAll(), "endpoints");
		Map<String,Object> model3 = render.toModel(AggregatedEndpointController.service.getAll(), "aggregated");
		Map<String,Object> model4 = render.toModel(HtmlViewsController.service.getAll(), "views");

		model.putAll(model2);
		model.putAll(model3);
		model.putAll(model4);
		return render.render(model, "routes.ftl");
	};

	public static final Route renderers = (Request request, Response response) -> {
		response.type("text/html");
		Map<String,Object> model = render.toModel(ViewRendererController.service.getAll(), "renderers");
		model.put("config", render.toModel(ViewRendererController.service.getSystemConfig()));
		return render.render(model, "renderers.ftl");
	};

	public static final Route templates = (Request request, Response response) -> {
		response.type("text/html");
		Map<String,Object> model = render.toModel(HtmlViewsController.service.getAll(), "views");
		return render.render(model, "templates.ftl");
	};
}
