package publisher.rest.model.endpoint.aggregated;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Entity;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.Endpoint;
import publisher.rest.model.endpoint.EndpointFormat;
import publisher.rest.model.endpoint.EndpointStatus;
import publisher.rest.model.endpoint.TriplestoreSparql;
import spark.Request;
import sparql.streamline.core.Sparql;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
public class DistributedSparql extends JsonWrapper{


	public DistributedSparql() {
		super();
		this.format = EndpointFormat.MULTIPLE;
	}

	@Override
	public void addEndpoint(AbstractEndpoint endpoint) throws EndpointFormatCompatibilityException {
		boolean isCompatible = (endpoint instanceof TriplestoreSparql);
		if(!isCompatible)
			throw new EndpointFormatCompatibilityException("Provided endpoint is not a TriplestoreSparql type");
		endpoints.add(endpoint);
	}

	@Override
	public String getData(Request request) throws EndpointRemoteDataException {
		// Load all RDF link endpoints into model
		Model model = ModelFactory.createDefaultModel();
		endpoints.parallelStream().map(endpoint -> toModel(endpoint, request)).forEach( subModel -> model.add(subModel));
		// Solve query over model
		Query query = buildQuery(request); // throws exception if required
		EndpointFormat formatProvided = EndpointFormat.retrieveFromSPARQLMime(request.headers("Accept"), query);
		ResultsFormat queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(formatProvided.toString());
		try {
			return new String(Sparql.queryModel(query.toString(), model, queryResultsFormat, null).toByteArray());
		} catch (Exception e) {
			throw new EndpointRemoteDataException(e.toString());
		}
	}

	@Override
	public String testEndpoint(Request request) throws EndpointFormatCompatibilityException {
		try {

			Query query = QueryFactory.create("SELECT * WHERE { ?s ?p ?o }");
			EndpointFormat formatProvided = EndpointFormat.JSON;
			ResultsFormat queryResultsFormat = EndpointFormat.retrieveSPARQLFormat(formatProvided.toString());
			Model model = ModelFactory.createDefaultModel();
			this.endpoints.forEach(endpoint -> model.read(new ByteArrayInputStream(endpoint.testData(request).getBytes()), null, endpoint.getFormat().toString()));
			String data = new String(Sparql.queryModel(query.toString(), model, queryResultsFormat, null).toByteArray());

			EndpointFormat.isFormatCompliant(data, formatProvided); // throws exeception
			this.status = EndpointStatus.VALID;
			return data;
		} catch (Exception e) {
			this.status = EndpointStatus.INVALID;
			throw new EndpointFormatCompatibilityException(e.toString());
		}
	}


	protected Model toModel(Endpoint endpoint, Request request) {
		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(endpoint.getData(request).getBytes()), null, endpoint.getFormat().toString());
		return model;
	}

	private Query buildQuery(Request request) {
		String query = null;
		// retrieve query from URL if GET, or body if POST, otherwise throw error
		if(request.requestMethod().equals("GET")) {
			query = request.queryParams("query");
		}else if(request.requestMethod().equals("POST")){
			query = request.body();
		}
		if(query==null)
			throw new EndpointRemoteDataException("SPARQL query are expected to be encoded in GET requests or in the body of a POST request.");

		Query queryBuilt = QueryFactory.create(query);
		List<String> endpointsURls = this.endpoints.parallelStream().map(end -> end.getUrl()).collect(Collectors.toList());
		if(queryBuilt.isAskType() || queryBuilt.isSelectType()) {
			query = rewriteQuery(query, endpointsURls);
		}else if(queryBuilt.isConstructType() || queryBuilt.isSelectType()) {
			throw new EndpointRemoteDataException("SPARQL CONSTRUCT or DESCRIBE are not currently supported");
		}

		EndpointFormat formatProvided = EndpointFormat.retrieveFromSPARQLMime(request.headers("Accept"), queryBuilt);
		checkFormatCompatibility(queryBuilt, formatProvided);

		return QueryFactory.create(query);
	}

	private void checkFormatCompatibility(Query queryBuilt, EndpointFormat format) {
		if(EndpointFormat.isRDF(format) && (queryBuilt.isAskType() || queryBuilt.isSelectType())) {
			throw new EndpointFormatCompatibilityException("Provided query is not compatible with the format provided("+format+")");
		}else if(!EndpointFormat.isRDF(format) && (queryBuilt.isConstructQuad() || queryBuilt.isConstructType() || queryBuilt.isDescribeType())) {
			throw new EndpointFormatCompatibilityException("Provided query is not compatible with the format provided("+format+")");
		}
	}


	private String rewriteQuery(String parsedQuery, List<String> endpoints) {
		int firstCut = parsedQuery.indexOf('{');
		int secondCut = parsedQuery.lastIndexOf('}');
		String serviceVar = ("?services-" + UUID.randomUUID().toString()).replaceAll("-", "");

		StringBuilder newQuery = new StringBuilder(parsedQuery.substring(0, firstCut + 1));

		newQuery.append("\nSERVICE ").append(serviceVar).append(" {").append(parsedQuery.substring(firstCut + 1, secondCut));
		newQuery.append("} VALUES ").append(serviceVar).append(" {");

		endpoints.parallelStream().filter(elem -> !elem.isBlank()).forEach(elem -> newQuery.append("<").append(elem).append("> "));
		newQuery.append(" }").append(parsedQuery.substring(secondCut));

		return newQuery.toString();
	}


}
