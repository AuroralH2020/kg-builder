package publisher.rest.model.endpoint.aggregated;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.Endpoint;
import spark.Request;


@JsonDeserialize(as=JsonWrapper.class)
public interface AggregatedEndpoint extends Endpoint {


	List<AbstractEndpoint> getEndpoints();

	@Override
	String getData(Request request) throws EndpointFormatCompatibilityException;

	void addEndpoint(AbstractEndpoint endpoint);

}