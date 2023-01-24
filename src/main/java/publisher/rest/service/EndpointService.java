package publisher.rest.service;

import java.util.Optional;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.endpoint.AbstractEndpoint;
import spark.Request;

public class EndpointService extends CRUDService<AbstractEndpoint> {

	public EndpointService() {
		super(AbstractEndpoint.class);
	}

	public String test(String id, Request request) {
		Optional<AbstractEndpoint> endpointOpt = this.getOptional(id);
		if (endpointOpt.isPresent()) {
			AbstractEndpoint endpoint = endpointOpt.get();
			try {
				String data = endpoint.testEndpoint(request);
				return data;
			} catch (Exception e) {
				throw new InvalidRequestException(e.getMessage());
			}finally {
				this.update(endpoint.getId(), endpoint);
			}
		} else {
			throw new InvalidRequestException("Requested endpoint does not exist");
		}
	}
}
