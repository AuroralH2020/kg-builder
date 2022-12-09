package publisher.rest.model.endpoint;

import java.net.URL;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import publisher.rest.PublisherRest;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.exception.InternalServiceException;
import publisher.rest.exception.InvalidRequestException;
import spark.Request;
import sparql.streamline.core.SparqlEndpoint;
import sparql.streamline.core.SparqlEndpointConfiguration;


@Entity
@JsonInclude(Include.NON_NULL)
public class TriplestoreDescribe extends Link {

	@JsonProperty
	protected String username;
	@JsonIgnore
	protected String password;
	@JsonProperty
	protected String aliasURL;

	@JsonProperty
	protected String publishedURL;

	public TriplestoreDescribe() {
		super();
	}


	@Override
	public void setFormat(EndpointFormat newFormat) {
		if(!EndpointFormat.isRDF(newFormat) && !EndpointFormat.MULTIPLE.equals(newFormat))
			throw new EndpointFormatCompatibilityException(this.getClass().getName()+" requires an RDF format since sends DESCRIBE queries, or MUTLIPLE if requestor can negotiate the output format with Accept header");
		this.format = newFormat;
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


	public String getPublishedURL() {
		return publishedURL;
	}



	public void setPublishedURL(String publishedURL) {
		try {
			new URL(publishedURL);
		}catch(Exception e) {
			throw new InvalidRequestException("Provided URL ("+publishedURL+") is not a valid URL");
		}
		this.publishedURL = publishedURL;
	}



	@JsonIgnore
	public void setPassword(String password) {
		this.password = password;
	}

	public String getAliasURL() {
		return aliasURL;
	}

	public void setAliasURL(String aliasURL) {
		try {
			new URL(aliasURL);
		}catch(Exception e) {
			throw new InvalidRequestException("Provided URL ("+aliasURL+") is not a valid URL");
		}
		this.aliasURL = aliasURL;
	}

	public TriplestoreDescribe(String name, EndpointFormat format, String url, String username, String password, String alias) {
		this.name = name;
		this.url = url;
		this.format = format;
		this.username = username;
		this.password = password;
		this.aliasURL = alias;
		if(EndpointFormat.JSONLD11.equals(format))
			throw new EndpointFormatCompatibilityException("Provided format ("+format.toString()+") is not compatible with the endpoint "+this.getClass().toString());

	}

	@Override
	public String getData(Request request) throws EndpointRemoteDataException{
		SparqlEndpoint endpoint = new SparqlEndpoint(new SparqlEndpointConfiguration(this.url, null, username, password));
		ResultsFormat queryResultsFormat = ResultsFormat.FMT_RDF_TURTLE; // default turtle
		String finalURL = changeURL(request);
		String query = "DESCRIBE <"+finalURL+">";
		if(this.format.equals(EndpointFormat.MULTIPLE)) { // it can change with content negotiation
			Query queryBuilt = QueryFactory.create(query);
			EndpointFormat format = EndpointFormat.retrieveFromSPARQLMime(request.headers("Accept"), queryBuilt);
			queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(format.toString());
		}else {
			// fixed result
			queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(this.format.toString());
		}
		System.out.println("TRIPLESTORE: "+query);
		try {
			return new String(endpoint.query(query, queryResultsFormat).toByteArray());
		} catch (Exception e) {
			throw new EndpointRemoteDataException("An exception occurred querying the triple store, possible cause: "+e.getMessage());
		}
	}



	private String changeURL(Request request) {
		String finalUrl = request.url();
		if(PublisherRest.CONFIG_MODE && request.queryParams("route")!=null)
			finalUrl = request.queryParams("route");
		if(aliasURL!=null && publishedURL!=null)
			finalUrl = finalUrl.replace(publishedURL, aliasURL);
		if((aliasURL!=null && publishedURL==null) || (aliasURL==null && publishedURL!=null))
			throw new InternalServiceException("Tirplestore ("+this.name+") is not correctly configured, if a 'URI to be replaced' is specified then a 'URI replacement for the triplestore' is mandatory and viceversa");
		return finalUrl;
	}

}
