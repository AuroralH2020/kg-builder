package publisher.rest.model.endpoint.aggregated;

import java.io.IOException;

import com.google.gson.JsonObject;

import publisher.service.DAOService;

public class AggregatedFactory {

	public static JsonWrapper createEndpoint(String jsonStr) throws IOException {
		JsonObject json = DAOService.castToJson(jsonStr);
		if(!json.has("@type"))
			throw new IllegalArgumentException("Key @type is mandatory but was not provided in payload: "+jsonStr);
		String type = json.get("@type").getAsString();
		if(type.equals("JsonWrapper")) {
			return (JsonWrapper) DAOService.fromJson(jsonStr, JsonWrapper.class);
		}else if(type.equals("AggregatedRDF")) {
			return DAOService.GSON.fromJson(jsonStr, AggregatedRDF.class);
		}else  if(type.equals("AggregatedJSONLD11")) {
			return DAOService.GSON.fromJson(jsonStr, AggregatedJSONLD11.class);
		}else  if(type.equals("SparqlOverLinks")) {
			return DAOService.GSON.fromJson(jsonStr, SparqlOverLinks.class);
		}else{
			throw new IllegalArgumentException("Provided @type does not match any existing type for aggregated endpoints, review it in the provided payload: "+jsonStr);
		}
	}
}
