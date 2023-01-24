package publisher.rest.model.endpoint;

import java.io.ByteArrayInputStream;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.validation.constraints.NotNull;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;

public enum EndpointFormat {
	// RDF Formats
	TURTLE("TURTLE"), TTL("TTL"), NTRIPLES("NTRIPLES"), NTRIPLE("NTRIPLE"), NT("NT"), RDFXML("RDFXML"), N3("N3"),
	JSONLD("JSONLD"), JSONLD11("JSONLD11"),
	// NonRDF
	CSV("CSV"), JSON("JSON"), TSV("TSV"), XML("XML"),
	// Unknown
	MULTIPLE("MULTIPLE"),;

	private final String format;

	EndpointFormat(final String format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return format;
	}

	public static boolean isRDF(EndpointFormat format) {
		switch (format) {
		case TURTLE:
			return true;
		case TTL:
			return true;
		case NTRIPLES:
			return true;
		case NTRIPLE:
			return true;
		case NT:
			return true;
		case RDFXML:
			return true;
		case N3:
			return true;
		case JSONLD:
			return true;
		case JSONLD11:
			return true;
		default:
			return false;
		}
	}

	public static boolean isNonRDF(EndpointFormat format) {
		switch (format) {
		case CSV:
			return true;
		case JSON:
			return true;
		case TSV:
			return true;
		case XML:
			return true;

		default:
			return false;
		}
	}

	static EndpointFormat fromString(String format) {
		if ("TURTLE".equals(format))
			return EndpointFormat.TURTLE;
		if ("TTL".equals(format))
			return EndpointFormat.TTL;
		if ("NTRIPLES".equals(format))
			return EndpointFormat.NTRIPLES;
		if ("NTRIPLE".equals(format))
			return EndpointFormat.NTRIPLE;
		if ("NT".equals(format))
			return EndpointFormat.NT;
		if ("RDFXML".equals(format))
			return EndpointFormat.RDFXML;
		if ("N3".equals(format))
			return EndpointFormat.N3;
		if ("JSONLD".equals(format))
			return EndpointFormat.JSONLD;
		if ("JSONLD11".equals(format))
			return EndpointFormat.JSONLD11;
		if ("CSV".equals(format))
			return EndpointFormat.CSV;
		if ("JSON".equals(format))
			return EndpointFormat.JSON;
		if ("TSV".equals(format))
			return EndpointFormat.TSV;
		if ("XML".equals(format))
			return EndpointFormat.XML;
		throw new IllegalArgumentException("Provided format " + format + " is not a valid EndpointFormat");
	}
	//	TODO: FINISH THIS COMPATIBILITY TEST
	public static void isFormatCompliant(@NotNull String content, @NotNull EndpointFormat format) {
		try {
			if (EndpointFormat.isRDF(format)) {
				ModelFactory.createDefaultModel().read(new ByteArrayInputStream(content.getBytes()), null,
						format.toString());
			} else if (EndpointFormat.isNonRDF(format)) {
				if (format.equals(EndpointFormat.JSON)) {
					isJsonCompliant(content);
				} else if (format.equals(EndpointFormat.XML)) {

				} else if (format.equals(EndpointFormat.CSV) || format.equals(EndpointFormat.TSV)) {

				} else {
					throw new EndpointFormatCompatibilityException(
							"Data provided is not compliant with the format " + format);
				}
			} else {
				throw new EndpointFormatCompatibilityException(
						"Provided format (" + format + ") is not one of the availabes");
			}
		} catch (Exception e) {
			throw new EndpointFormatCompatibilityException(
					"Data provided is not compliant with the format " + format + ", hint: " + e.toString());
		}
	}

	private static boolean isJsonCompliant(String jsonStr) {
		String error = "";
		try {
		(new Gson()).fromJson(jsonStr, JsonObject.class);
		return true;
		}catch(Exception e1) {
			error = e1.getMessage();
			try {
				(new Gson()).fromJson(jsonStr, JsonArray.class);
				return true;
			} catch(Exception e2) {
				error += " "+e2.getMessage();
				throw new EndpointFormatCompatibilityException(error);
			}
		}
	}

	public static ResultsFormat retrieveSPARQLFormat(String format) {
		ResultsFormat queryResultsFormat  = null;
		if(EndpointFormat.TURTLE.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_TURTLE;
		else if(EndpointFormat.TTL.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_TTL;
		else if(EndpointFormat.RDFXML.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_XML;
		else if(EndpointFormat.NTRIPLES.toString().equals(format) || EndpointFormat.NTRIPLE.toString().equals(format) || EndpointFormat.NT.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_NT;
		else if(EndpointFormat.N3.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_N3;
		else if(EndpointFormat.JSONLD.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_JSONLD;
		else if(EndpointFormat.CSV.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RS_CSV;
		else if(EndpointFormat.JSON.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RS_JSON;
		else if(EndpointFormat.TSV.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RS_TSV;
		else if(EndpointFormat.XML.toString().equals(format))
			queryResultsFormat =ResultsFormat.FMT_RDF_XML;
		else
			throw new EndpointFormatCompatibilityException("Provided format ("+format.toString()+") is not valid");
		return queryResultsFormat;

	}

	public static EndpointFormat retrieveFromSPARQLMime(String accept, Query queryBuilt) {
		boolean nonSemanticResult = (queryBuilt.isSelectType() || queryBuilt.isAskType());
		boolean isSemanticResult = (queryBuilt.isConstructType() || queryBuilt.isConstructQuad()|| queryBuilt.isDescribeType());
		if(nonSemanticResult) {
			if(accept.contains("application/sparql-results+json") || accept.contains("application/json"))
				return EndpointFormat.JSON;
			if(accept.contains("text/csv"))
				return EndpointFormat.CSV;
			if(accept.contains("text/tsv") || accept.contains("text/tab-separated-values"))
				return EndpointFormat.TSV;
			if(accept.contains("sparql-results+xml") || accept.contains("application/xml"))
				return EndpointFormat.XML;
			return EndpointFormat.JSON;
		}else if(isSemanticResult) {
			if(accept.contains("text/turtle") || accept.contains("application/x-turtle"))
				return EndpointFormat.TURTLE;
			if(accept.contains("text/rdf+n3") || accept.contains("text/n3"))
				return EndpointFormat.N3;
			if(accept.contains("text/plain") )
				return EndpointFormat.NT;
			if(accept.contains("application/rdf+xml") || accept.contains("application/xml"))
				return EndpointFormat.RDFXML;
			if(accept.contains("application/json+ld") || accept.contains("application/json"))
				return EndpointFormat.JSONLD;
			return EndpointFormat.NT;
		}else {
			throw new EndpointRemoteDataException("The query specified is not supported, specify one of SELECT, CONSTRUCT, ASK, or DESCRIBE. Provided query: "+queryBuilt.toString());
		}


	}

}
