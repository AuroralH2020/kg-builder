package publisher.rest.model.endpoint;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.JsonObject;

import publisher.rest.model.Datasource;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;

@JsonDeserialize(as=AbstractEndpoint.class)
public interface Endpoint extends Datasource {

	String getUrl();
	
	void setUrl(String url);
	
	EndpointStatus getStatus();

	void setStatus(EndpointStatus status);

	EndpointFormat getFormat();

	String getId();

	public JsonObject toJson();
}
