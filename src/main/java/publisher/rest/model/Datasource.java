package publisher.rest.model;

import publisher.rest.exception.EndpointRemoteDataException;
import spark.Request;

public interface Datasource {
	String getData(Request request) throws EndpointRemoteDataException;
	String testEndpoint(Request request) throws EndpointRemoteDataException;
}
