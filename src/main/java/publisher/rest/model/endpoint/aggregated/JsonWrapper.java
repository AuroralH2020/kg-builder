package publisher.rest.model.endpoint.aggregated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;
import org.apache.jena.ext.com.google.common.collect.Maps;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.exception.EndpointRemoteDataException;
import publisher.rest.model.AbstractDatasource;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.Endpoint;
import publisher.rest.model.endpoint.EndpointFormat;
import publisher.rest.model.endpoint.EndpointStatus;
import spark.Request;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({ @Type(AggregatedRDF.class), @Type(AggregatedJSONLD11.class), @Type(SparqlOverLinks.class) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class JsonWrapper extends AbstractDatasource implements AggregatedEndpoint {



	@NotNull
	@JsonProperty
	// @OneToMany(targetEntity= AbstractEndpoint.class, fetch = FetchType.EAGER,
	// orphanRemoval = true) // , fetch = FetchType.EAGER
//	@ManyToMany(targetEntity = AbstractEndpoint.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE) // , fetch = FetchType.EAGER,
//	@JoinTable(name = "aggregated_endpoint_relationships", joinColumns = {
//            @JoinColumn(name = "aggregated", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
//            @JoinColumn(name = "endpoint", referencedColumnName = "id", nullable = true)})
	@ManyToMany(targetEntity= AbstractEndpoint.class, fetch = FetchType.EAGER, cascade=CascadeType.DETACH)
	@JoinColumn(name = "aggregation", referencedColumnName = "id", nullable = true)
	protected List<AbstractEndpoint> endpoints = Lists.newArrayList();

	public JsonWrapper() {
		super();
		format = EndpointFormat.JSON;
	}

	@Override
	public List<AbstractEndpoint> getEndpoints() {
		return endpoints;
	}

	@Override
	public String testEndpoint(Request request) throws EndpointFormatCompatibilityException {
		try {
			String data = fetchData( request, true);
			EndpointFormat.isFormatCompliant(data, format); // throws exeception
			this.status = EndpointStatus.VALID;
			return data;
		} catch (Exception e) {
			this.status = EndpointStatus.INVALID;
			throw new EndpointFormatCompatibilityException(e.toString());
		}
	}

	@Override
	public String getData(Request request) throws EndpointRemoteDataException {
		return fetchData( request, false);
	}

	private String fetchData(Request request, boolean test) {
		Map<String, String> aggregatedMap = endpoints.parallelStream()
				.flatMap(endpoint -> toMap(endpoint, request, test).entrySet().parallelStream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (key1, key2) -> {
					return key1;
				}));

		return GSON.toJson(aggregatedMap);
	}

	private Map<String, String> toMap(Endpoint endpoint, Request request, boolean test) {
		Map<String, String> map = Maps.newHashMap();
		try {
			String data = "";
			if(test) {
				data = endpoint.testEndpoint(request);
			}else {
				data = endpoint.getData(request);
			}
			map.put(endpoint.getId().replace(' ', '_'), data);
			endpoint.setStatus(EndpointStatus.VALID);
		} catch (Exception e) {
			System.out.println("Exception with " + endpoint.getClass().getName() + " " + endpoint.toString());
			System.out.println(e.toString());
			map.put("partial_content", "true");
			endpoint.setStatus(EndpointStatus.INVALID);
		}

		return map;
	}

	@Override
	public void addEndpoint(AbstractEndpoint endpoint) {
		this.endpoints.add(endpoint);
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUrl(String url) {
		// TODO Auto-generated method stub

	}




}
