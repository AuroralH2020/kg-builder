package publisher.rest.model.endpoint;


import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import spark.Request;
import sparql.streamline.core.SparqlEndpoint;
import sparql.streamline.core.SparqlEndpointConfiguration;

@Entity
@JsonInclude(Include.NON_NULL)
public class SPARQL extends Link {

	@JsonProperty
	protected String username;
	@JsonIgnore
	protected String password;
	@JsonProperty
	protected String query;
	@JsonIgnore
	@Transient
	protected Query queryBuilt;

	public SPARQL() {
		super();
	}

	public SPARQL(String name, String query, EndpointFormat format, String url, String username, String password) {
		this.name = name;
		this.url = url;
		this.format = format;
		this.queryBuilt = QueryFactory.create(query);
		this.query = queryBuilt.toString();
		this.username = username;
		this.password = password;
		checkFormatCompatibility();
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

	private void checkFormatCompatibility() {
		if(EndpointFormat.isRDF(this.format) && (queryBuilt.isAskType() || queryBuilt.isSelectType())) {
			throw new EndpointFormatCompatibilityException("Provided query is not compatible with the format provided("+this.format+")");
		}else if(EndpointFormat.isRDF(this.format) && (queryBuilt.isConstructQuad() || queryBuilt.isConstructType() || queryBuilt.isDescribeType())) {
			throw new EndpointFormatCompatibilityException("Provided query is not compatible with the format provided("+this.format+")");
		}
	}

	@Override
	public String getData(Request request) throws EndpointRemoteDataException{
		SparqlEndpoint endpoint = new SparqlEndpoint(new SparqlEndpointConfiguration(this.url, null, username, password));
		ResultsFormat queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(this.format.toString());
		try {
			return new String(endpoint.query(query, queryResultsFormat).toByteArray());
		} catch (Exception e) {
			throw new EndpointRemoteDataException(e.toString());
		}
	}





}
