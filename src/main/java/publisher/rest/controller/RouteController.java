package publisher.rest.controller;

import java.util.Optional;
import java.util.stream.Collectors;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.PublisherRoute;
import publisher.rest.service.DAOService;
import publisher.rest.service.RouteService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RouteController {

	public static RouteService service = new RouteService();

	public static final Route list = (Request request, Response response) -> {
		response.type("application/json");
		response.status(200);
		return service.getAll().parallelStream().collect(Collectors.toList());

	};

	public static final Route get = (Request request, Response response) -> {
		String id = fetchId(request);
		response.status(200);
		response.type("application/json");
		return DAOService.toJson(service.get(id));
	};

	public static final Route createUpdate = (Request request, Response response) -> {
		response.status(200);
		response.type("application/json");
		PublisherRoute route = service.createUpdate(request.body());
		if(route!=null) {
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




	public static final Route publish = (Request request, Response response) -> {
		Optional<PublisherRoute> virtualDomain = service.getAll().parallelStream().filter(domain -> domain.matches(request.url())).findFirst();
		if (virtualDomain.isPresent()) {
			try {
				PublisherRoute domain = virtualDomain.get();
				response.status(200);
				return domain.solve(request);
			} catch (Exception e) {
				throw new InvalidRequestException(e.getMessage());
			}
		} else {
			throw new InvalidRequestException("There is no virtual domain that matches the provided domain.");
		}
	};

	public static final Route test = (Request request, Response response) -> {
		String targetDomain = request.queryParams("route");
		if(targetDomain==null || targetDomain.isEmpty())
			throw new InvalidRequestException("test endpoint must have query parameter route, e.g., /test?route=*");

		Optional<PublisherRoute> virtualDomain = service.getAll().parallelStream().filter(domain -> domain.matches(targetDomain)).findFirst();
		if (virtualDomain.isPresent()) {
			try {
				PublisherRoute domain = virtualDomain.get();
				response.status(200);
				return domain.solve(request);
			} catch (Exception e) {
				throw new InvalidRequestException(e.getMessage());
			}
		} else {
			throw new InvalidRequestException("There is no virtual domain that matches the provided domain.");
		}
	};

	protected static final String fetchId(Request request) {
		String id = request.params("id");
		if (id == null || id.isEmpty())
			throw new InvalidRequestException("Provided id can not be blank or null");
		return id;
	}



}
