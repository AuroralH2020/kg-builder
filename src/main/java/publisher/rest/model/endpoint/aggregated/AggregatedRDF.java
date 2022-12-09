package publisher.rest.model.endpoint.aggregated;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.Endpoint;
import publisher.rest.model.endpoint.EndpointFormat;
import spark.Request;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({ @Type(AggregatedJSONLD11.class) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AggregatedRDF extends JsonWrapper {

	public AggregatedRDF(){
		super();
	}

	@Override
	public void addEndpoint(AbstractEndpoint endpoint) throws EndpointFormatCompatibilityException {
		if(!EndpointFormat.isRDF(endpoint.getFormat()))
			throw new EndpointFormatCompatibilityException("Requested endpoint does not provide RDF data");
		endpoints.add(endpoint);
	}

	@Override
	public String getData(Request request) throws EndpointRemoteDataException {
		Model model = ModelFactory.createDefaultModel();
		endpoints.parallelStream().map(endpoint -> toModel(endpoint, request)).forEach( subModel -> model.add(subModel));
		Writer writer = new StringWriter();
		model.write(writer, this.format.toString(), null);
		return writer.toString();
	}

	protected Model toModel(Endpoint endpoint, Request request) {
		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(endpoint.getData(request).getBytes()), null, endpoint.getFormat().toString());
		return model;
	}







}
