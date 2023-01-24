package publisher.rest.model.endpoint;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import spark.Request;
import sparql.streamline.core.SparqlEndpoint;
import sparql.streamline.core.SparqlEndpointConfiguration;

@Entity
@JsonInclude(Include.NON_NULL)
public class TriplestoreSparql extends Link{

	@JsonProperty
	protected String username;
	@JsonIgnore
	protected String password;



	public TriplestoreSparql() {
		super();
		this.format = EndpointFormat.MULTIPLE;
	}

	public TriplestoreSparql(String name, EndpointFormat format,  String url, String username, String password) {
		this.name = name;
		this.url = url;
		this.username = username;
		this.password = password;
		if(!EndpointFormat.MULTIPLE.equals(format))
			throw new EndpointFormatCompatibilityException("Provided format for 'Triplestore Sparql' must be MULTIPLE, but another was provided.");
		this.format = EndpointFormat.MULTIPLE;

	}

	@Override
	public void setFormat(EndpointFormat newFormat) {
		if(!EndpointFormat.MULTIPLE.equals(newFormat))
			throw new EndpointFormatCompatibilityException("Provided format for 'Triplestore Sparql' must be MULTIPLE, but another was provided.");

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	@JsonIgnore
	public void setPassword(String password) {
		this.password = password;
	}

	private void checkFormatCompatibility(Query queryBuilt, EndpointFormat format) {
		if(EndpointFormat.isRDF(format) && (queryBuilt.isAskType() || queryBuilt.isSelectType())) {
			throw new EndpointFormatCompatibilityException("Provided query is not compatible with the format provided("+format+")");
		}else if(!EndpointFormat.isRDF(format) && (queryBuilt.isConstructQuad() || queryBuilt.isConstructType() || queryBuilt.isDescribeType())) {
			throw new EndpointFormatCompatibilityException("Provided query is not compatible with the format provided("+format+")");
		}
	}

	@Override
	public String getData(Request request) throws EndpointRemoteDataException{
		String query = extractQuery(request);
		Query queryBuilt = QueryFactory.create(query);
		EndpointFormat formatProvided = EndpointFormat.retrieveFromSPARQLMime(request.headers("Accept"), queryBuilt);
		checkFormatCompatibility(queryBuilt, formatProvided);
		SparqlEndpoint endpoint = new SparqlEndpoint(new SparqlEndpointConfiguration(this.url, null, username, password));
		ResultsFormat queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(formatProvided.toString());
		try {
			return new String(endpoint.query(query, queryResultsFormat).toByteArray());
		} catch (Exception e) {
			throw new EndpointRemoteDataException(e.toString());
		}
	}


	private String extractQuery(Request request) {
		String query = null;
		// retrieve query from URL if GET, or body if POST, otherwise throw error
		if(request.requestMethod().equals("GET")) {
			query = request.queryParams("query");
		}else if(request.requestMethod().equals("POST")){
			query = request.body();
		}
		if(query==null)
			throw new EndpointRemoteDataException("SPARQL query are expected to be encoded in GET requests or in the body of a POST request.");
		return query;
	}

	@Override
	public String testData(Request request) throws EndpointRemoteDataException {
		try {

			Query queryBuilt = QueryFactory.create( "SELECT * { ?s ?p ?o }");
			SparqlEndpoint endpoint = new SparqlEndpoint(new SparqlEndpointConfiguration(this.url, null, username, password));
			ResultsFormat queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(EndpointFormat.JSON.toString());
			this.status = EndpointStatus.VALID;
			return new String(endpoint.query(queryBuilt.toString(), queryResultsFormat).toByteArray());
		} catch (Exception e) {
			this.status = EndpointStatus.INVALID;
			throw new EndpointRemoteDataException(e.toString());
		}

	}

}
