package publisher.rest.model.endpoint;

import java.io.IOException;

import com.google.gson.JsonObject;

import publisher.rest.model.endpoint.aggregated.AggregatedJSONLD11;
import publisher.rest.model.endpoint.aggregated.AggregatedRDF;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;
import publisher.rest.model.endpoint.aggregated.SparqlOverLinks;
import publisher.service.DAOService;

public class EndpointFactory {


	public static Endpoint createEndpoint(String name, EndpointFormat format, String link) {
		return new Link(name, link, format);

	}

	public static Endpoint createEndpoint(String name, EndpointFormat format, String query, String link, String username, String password, String alias) {
		if(query==null) {
			return new TriplestoreDescribe(name,format, link, username, password, alias);
		}else {
			return new SPARQL(name, query, format, link, username, password);
		}
	}
	
	public static AbstractEndpoint createEndpoint(String jsonStr) throws IOException {
		JsonObject json = DAOService.castToJson(jsonStr);
		if(!json.has("@type"))
			throw new IllegalArgumentException("Key @type is mandatory but was not provided in payload: "+jsonStr);
		String type = json.get("@type").getAsString();
		if(type.equals("Link")) {
			return DAOService.GSON.fromJson(jsonStr, Link.class);
		}else if(type.equals("SPARQL")) {
			return DAOService.GSON.fromJson(jsonStr, SPARQL.class);
		}else  if(type.equals("TriplestoreDescribe")) {
			return DAOService.GSON.fromJson(jsonStr, TriplestoreDescribe.class);
		}else  if(type.equals("TriplestoreSparql")) {
			return DAOService.GSON.fromJson(jsonStr, TriplestoreSparql.class);
		}else{
			throw new IllegalArgumentException("Provided @type does not match any existing type for endpoints, review it in the provided payload: "+jsonStr);
		}
	}


}
