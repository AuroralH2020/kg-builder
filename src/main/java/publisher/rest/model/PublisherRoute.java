package publisher.rest.model;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.jena.ext.com.google.common.collect.Maps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import publisher.rest.exception.InvalidRequestException;
import publisher.service.DAOService;
import spark.Request;


@Entity
@JsonInclude(Include.NON_NULL)
//@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME)
public class PublisherRoute {

	@Id
	@JsonProperty
	protected String id = UUID.randomUUID().toString();

	@JsonIgnore
	@Transient
	public static final Gson GSON = new Gson();

	@JsonProperty
	@Column(unique = true)
	private String route;

	@JsonProperty
	@ManyToOne(targetEntity= AbstractDatasource.class, fetch = FetchType.EAGER, cascade=CascadeType.DETACH)
	private AbstractDatasource endpoint;


	@NotNull
	@JsonProperty
	private Boolean isRegex;

	//@OneToOne(targetEntity= HtmlView.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = true)
	@ManyToOne(targetEntity= HtmlView.class, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name="viewId")
	private HtmlView view;

	public PublisherRoute() {
		super();
	}

	public PublisherRoute(String domain, Boolean isRegex) {
		super();
		this.route = domain;
		this.isRegex = isRegex;
	}

	public boolean matches(String url) {
		if(isRegex) {
			return url.matches(route);
		}else {
			return route.equals(url);
		}
	}


	public String solve(Request request) {
		if(!request.headers("Accept").contains("html") || this.view==null) {
			if(endpoint!=null) {
				return endpoint.getData(request);
			}else {
				throw new InvalidRequestException("The requested domain has no endpoint assigned");
			}
		}else {
			if(view!=null) {
				Map<String,Object> values = Maps.newHashMap();
				if(endpoint!=null)
					values.put("data", endpoint.getData(request));
				
				return this.view.render.render(values, view.template);
			}else {
				throw new InvalidRequestException("The requested domain has no HTML template assigned");
			}
		}

	}

	


	public HtmlView getView() {
		return view;
	}

	public void setView(HtmlView view) {
		this.view = view;
	}

	public String getId() {
		return id;
	}

	@JsonIgnore
	public void setId(String id) {
		this.id = id;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public Boolean getIsRegex() {
		return isRegex;
	}

	public void setIsRegex(Boolean isRegex) {
		this.isRegex = isRegex;
	}



	public AbstractDatasource getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(AbstractDatasource endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public int hashCode() {
		return Objects.hash(route);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		PublisherRoute other = (PublisherRoute) obj;
		return Objects.equals(route, other.route);
	}

	public JsonObject toJson() {
		return GSON.fromJson(this.toString(), JsonObject.class);
	}

	@Override
	public String toString() {
		return DAOService.toJson(this);
	}




}
