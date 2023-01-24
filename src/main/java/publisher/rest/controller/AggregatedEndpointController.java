package publisher.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.endpoint.EndpointStatus;
import publisher.rest.model.endpoint.aggregated.AggregatedFactory;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;
import publisher.rest.service.AggregatedEndpointService;
import publisher.rest.service.DAOService;
import publisher.rest.service.RouteService;
import spark.Request;
import spark.Response;
import spark.Route;

public class AggregatedEndpointController {

	public static AggregatedEndpointService service = new AggregatedEndpointService();
	public static RouteService routeService = new RouteService();

	public static final Route list = (Request request, Response response) -> {
		List<String> endpoints = service.getAll().parallelStream().map(endp -> endp.toJson()).map(elem -> elem.toString())
				.collect(Collectors.toList());
		return endpoints;
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
		try {
			JsonObject bodyJson = DAOService.castToJson(request.body());
			List<String> endpointIdsList = new ArrayList<>();
			if(bodyJson.has("endpoints"))
				bodyJson.remove("endpoints").getAsJsonArray().forEach(elem -> endpointIdsList.add(elem.getAsString()));

			JsonWrapper endpoint = AggregatedFactory.createEndpoint(bodyJson.toString());
			endpoint.setStatus(EndpointStatus.UNTESTED);
			JsonWrapper updated = service.update(String.valueOf(endpoint.getId()), endpoint, endpointIdsList);
			if(updated!=null) {
				return DAOService.toJson(updated);
			}else {
				response.status(201);
				return "";
			}
		}catch(Exception e) {
			throw new InvalidRequestException(e.getMessage());
		}

	};

	public static final Route remove = (Request request, Response response) -> {
		String id = fetchId(request);
		if (service.exist(id)) {
			routeService.getAll().parallelStream()
				.forEach(route -> {
					if(route.getEndpoint()!=null && route.getEndpoint().getId().equals(id)) {
						route.setEndpoint(null);
						routeService.update(route);
					}
				});

			service.delete(id);
			response.status(200);
		} else {
			response.status(404);
		}
		return "";
	};




	public static final Route test = (Request request, Response response) -> {
		String id = fetchId(request);
		response.status(200);
		try {
			return service.test(id,request);
		}catch(Exception e) {
			throw new InvalidRequestException(e.getMessage());
		}
	};

	protected static final String fetchId(Request request) {
		String id = request.params("id");
		if (id == null || id.isEmpty())
			throw new InvalidRequestException("Requested id does not exist");
		return id;
	}


}
