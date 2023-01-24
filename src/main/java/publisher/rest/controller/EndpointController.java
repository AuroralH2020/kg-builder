package publisher.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.EndpointFactory;
import publisher.rest.model.endpoint.EndpointStatus;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;
import publisher.rest.service.DAOService;
import publisher.rest.service.EndpointService;
import publisher.rest.service.RouteService;
import spark.Request;
import spark.Response;
import spark.Route;

public class EndpointController {

	public static EndpointService service = new EndpointService();
	public static RouteService routeService = new RouteService();

	public static final Route list = (Request request, Response response) -> {
		response.type("application/json");
		response.status(200);
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
		String body = request.body();
		if(body==null || body.isEmpty())
			throw new InvalidRequestException("Provide a valid endpoint to be created/updated, no endpoint was provided.");
		try {
			AbstractEndpoint endpoint = EndpointFactory.createEndpoint(body);
			endpoint.setStatus(EndpointStatus.UNTESTED);
			boolean existed = service.exist(endpoint.getId());
			service.update(endpoint);
			if(!existed) {
				return DAOService.toJson(endpoint);
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
			// Remove the endpoint first from aggregated endpoints that rely on it
			List<JsonWrapper> aggregated = AggregatedEndpointController.service.getAll().parallelStream().filter(elem -> elem.getEndpoints().parallelStream().filter(end->end.getId().equals(id)).findFirst().isPresent()).collect(Collectors.toList());
			for(int index=0; index < aggregated.size(); index++) {
				JsonWrapper aggregatedEndpoint = aggregated.get(index);
				int counter=0;
				for(counter=0; counter < aggregatedEndpoint.getEndpoints().size(); counter++) {
					if(aggregatedEndpoint.getEndpoints().get(counter).getId().equals(id))
						break;
				}
				aggregatedEndpoint.getEndpoints().remove(counter);
				AggregatedEndpointController.service.update(aggregatedEndpoint);
			}
			// Detach routes associated
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
		try{
			response.status(200);
			return service.test(id, request);
		} catch(Exception e) {
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
