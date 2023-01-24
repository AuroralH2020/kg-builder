package publisher.rest.controller;

import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.HtmlView;
import publisher.rest.service.DAOService;
import publisher.rest.service.HtmlViewsService;
import spark.Request;
import spark.Response;
import spark.Route;

public class HtmlViewsController {

	public static HtmlViewsService service = new HtmlViewsService();

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

		JsonObject view = DAOService.castToJson(request.body());
		if(!view.has("render"))
			throw new InvalidRequestException("view must have a mandatory key 'render'");
		String renderId = view.remove("render").getAsString();
		HtmlView htmlView = (HtmlView) DAOService.fromJson(view.toString(), HtmlView.class);
		boolean route = service.update(htmlView.getId(), htmlView, renderId);
		if(!route) {
			return DAOService.toJson(route);
		}else {
			response.status(201);
			return "";
		}

	};

	public static final Route remove = (Request request, Response response) -> {
		String id = fetchId(request);
		if (service.exist(id)) {
			service.delete(id);
			response.status(200);
		} else {
			response.status(404);
		}
		return "";
	};






	protected static final String fetchId(Request request) {
		String id = request.params("id");
		if (id == null || id.isEmpty())
			throw new InvalidRequestException("Provided id can not be blank or null");
		return id;
	}
}
