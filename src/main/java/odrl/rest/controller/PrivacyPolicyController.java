package odrl.rest.controller;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import odrl.rest.exception.InvalidRequestException;
import odrl.rest.model.PolicyDocument;
import odrl.rest.persistence.Repository;
import spark.Request;
import spark.Response;
import spark.Route;

public class PrivacyPolicyController {
	
	private static Repository<PolicyDocument> repository = new Repository<>(PolicyDocument.class);

	public static final Route get = (Request request, Response response) -> {
		String id = fetchId(request);
		Optional<PolicyDocument> documentOpt = repository.retrieve(id);
		if (documentOpt.isPresent()) {
			response.status(200);
			response.type("text/turtle");
			return documentOpt.get().policyDocument;
		} else {
			response.status(404);
			return "";
		}
	};
	
	public static final Route create = (Request request, Response response) -> {
		String id = fetchId(request);
		String body = request.body();
		if (body.isBlank())
			throw new InvalidRequestException("An ODRL policy must be provided in the body");
		PolicyDocument doc = new PolicyDocument(id, body);
		response.status(201);
		if (repository.exists(id)) {
			response.status(204);
			repository.delete(id);
		}
		try {
			repository.persist(doc);
			return doc;
		} catch (Exception e) {
			throw new InvalidRequestException(e.toString());
		}
	};
	
	public static final Route remove = (Request request, Response response) -> {
		String id = fetchId(request);
		if (repository.exists(id)) {
			repository.delete(id);
			response.status(200);
		} else {
			response.status(404);
		}
		return "";
	};
	
	public static final Route apply = (Request request, Response response) -> {
		String id = fetchId(request);
		String body = request.body();
		String format = request.queryParams("format").toLowerCase();
		if (format == null || format.isEmpty() || (!format.equals("turtle") && !format.equals("json-ld 1.1"))) {
			throw new InvalidRequestException("Provide a valid format argument: json-ld or turtle");
		}
		// Retrieve
		Optional<PolicyDocument> documentOpt = repository.retrieve(id);
		if (documentOpt.isPresent()) {
			
			return null;
		} else {
			throw new InvalidRequestException("The id belongs to no validation document");
		}
	};
	
	protected static final String fetchId(Request request) {
		String id = request.params("id");
		if (id == null || id.isEmpty())
			throw new InvalidRequestException("Missing valid Component id");
		return id;
	}
}
