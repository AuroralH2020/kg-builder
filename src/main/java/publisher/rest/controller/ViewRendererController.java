package publisher.rest.controller;

import java.util.stream.Collectors;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.SystemConfig;
import publisher.rest.model.renderers.AbstractRenderer;
import publisher.rest.service.DAOService;
import publisher.rest.service.ViewRendererService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewRendererController {

	public static ViewRendererService service = new ViewRendererService();


	public static final Route getSystemConfig = (Request request, Response response) -> {
		response.type("application/json");
		response.status(200);
		return service.getSystemConfig();
	};

	public static final Route updateSystemConfig = (Request request, Response response) -> {
		response.type("application/json");
		response.status(201);
		SystemConfig newConfig = (SystemConfig) DAOService.fromJson(request.body(), SystemConfig.class);
		service.updateSystemConfig(newConfig);
		return "";
	};



	public static final Route list = (Request request, Response response) -> {
		response.type("application/json");
		response.status(200);
		return service.getAll().parallelStream().map(dom -> DAOService.toJson(dom)).map(elem -> elem.toString())
				.collect(Collectors.toList());

	};

	public static final Route get = (Request request, Response response) -> {
		String id = fetchId(request);
		response.status(200);
		response.type("application/json");
		return DAOService.toJson(service.get(id));

	};

	public static final Route update = (Request request, Response response) -> {
		response.status(200);
		response.type("application/json");
		System.out.println(">"+request.body());
		try {
			AbstractRenderer viewRenderer = AbstractRenderer.create(DAOService.castToJson(request.body()));
			boolean existed = service.update(String.valueOf(viewRenderer.getId()), viewRenderer);
			if(!existed) {
				return DAOService.toJson(viewRenderer);
			}else {
				response.status(201);
				return "";
			}
		}catch(Exception e) {
			throw new InvalidRequestException(e.getMessage());
		}
	};

	protected static final String fetchId(Request request) {
		String id = request.params("id");
		if (id == null || id.isEmpty())
			throw new InvalidRequestException("Provided id can not be blank or null");
		return id;
	}

	public static void init() {
		try {
			service.init();
		}catch(Exception e) {
			System.out.println("Renderers throw an exception: "+e.toString());
			System.exit(-1);
		}
	}



}
