package publisher.rest.model.endpoint;

import publisher.rest.exception.EndpointRemoteDataException;
import spark.Request;

public interface TesteableEndpoint {

	String testData(Request request) throws EndpointRemoteDataException;

}
