package publisher.rest.model.endpoint.aggregated;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.JsonLdVersion;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.model.endpoint.EndpointFormat;
import spark.Request;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
public class AggregatedJSONLD11 extends AggregatedRDF {

	@NotBlank
	@JsonProperty
	private String frame;

	public AggregatedJSONLD11() {
		super();
		this.format = EndpointFormat.JSONLD11;
	}

	public AggregatedJSONLD11(String frame) {
		this.format = EndpointFormat.JSONLD11;
		this.frame = frame;
	}

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}





	@Override
	public String getData(Request request) throws EndpointRemoteDataException {
		Model model = ModelFactory.createDefaultModel();
		endpoints.parallelStream().map(endpoint -> toModel(endpoint, request)).forEach( subModel -> model.add(subModel));
		Writer writer = new StringWriter();
		model.write(writer, "JSONLD");
		return toJsonLd11(writer.toString(), frame);
	}

	private String toJsonLd11(String jsonLd, String frame) {
		try {
			Document document = JsonDocument.of(new StringReader(jsonLd));
			Document frameDocument = JsonDocument.of(new StringReader(frame));
			JsonLdOptions options = new JsonLdOptions();
			//options.setBase(new URI(Directory.getConfiguration().getService().getDirectoryURIBase()));
			options.setCompactArrays(true); // // IMPORTANT FOR WOT TEST SUITE

			options.setCompactToRelative(true);
			options.setExplicit(false);
			options.setProduceGeneralizedRdf(true);
			options.setProcessingMode(JsonLdVersion.V1_1);
			return JsonLd.frame(document, frameDocument).options(options).get().toString();
		}catch(Exception e) {
			//throw new InternalServiceException("Error translating RDF endpoints under "+this.getDomain()+" into JSON-LD 1.1");
			e.printStackTrace();
			return null;
		}
	}



}
