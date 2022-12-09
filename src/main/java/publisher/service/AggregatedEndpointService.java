package publisher.service;

import java.util.List;
import java.util.Optional;

import org.apache.jena.ext.com.google.common.collect.Lists;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.Endpoint;
import publisher.rest.model.endpoint.EndpointStatus;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;
import spark.Request;

public class AggregatedEndpointService extends CRUDService<JsonWrapper>{

	private EndpointService service = new EndpointService();


	public AggregatedEndpointService() {
		super(JsonWrapper.class);
	}


	public JsonWrapper update(String id, JsonWrapper aggregated, List<String> endpointIdsList) {
		boolean existed = this.exist(id);
		endpointIdsList.parallelStream()
					.map(endpointId -> service.get(endpointId))
					.filter(endpoint -> !aggregated.getEndpoints().contains(endpoint))
					.forEach(endpoint -> aggregated.addEndpoint(endpoint));
		this.update(id, aggregated);
		if(existed)
			return aggregated;
		return null;

	}

	public String test(String id, Request request) {
		Optional<JsonWrapper> aggregatedOpt = this.getOptional(id);
		if (aggregatedOpt.isPresent()) {
			JsonWrapper aggregated = aggregatedOpt.get();
			try {
				String data = aggregated.testEndpoint(request);
				aggregated.getEndpoints().parallelStream().forEach(end -> System.out.println(end.getId()+" "+end.getStatus()));
				return data;
			} catch (Exception e) {
				aggregated.setStatus(EndpointStatus.INVALID);
				throw new InvalidRequestException(e.getMessage());
			}finally {
				System.out.println("PUBLISHER CONFIG [TESTING] "+aggregated);
				//aggregated.getEndpoints().forEach(endpoint -> service.update(endpoint.getId(), endpoint));
				this.update(aggregated.getId(),aggregated);
			}
		} else {
			throw new InvalidRequestException("Requested endpoint does not exist");
		}
	}
	



}
