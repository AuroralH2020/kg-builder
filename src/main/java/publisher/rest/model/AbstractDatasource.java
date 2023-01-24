package publisher.rest.model;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import publisher.rest.model.endpoint.EndpointFormat;
import publisher.rest.model.endpoint.EndpointStatus;
import publisher.rest.service.DAOService;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME)
public abstract class AbstractDatasource implements Datasource{

	@JsonIgnore
	@Transient
	public static final Gson GSON = new Gson();

	@Id
	@JsonProperty
	protected String id = UUID.randomUUID().toString();

	@NotNull
	@Enumerated(EnumType.STRING)
	@JsonProperty
	protected EndpointFormat format;

	@NotBlank
	@NotNull
	@JsonProperty
	protected String name;

	@NotNull
	@Enumerated(EnumType.STRING)
	@JsonProperty
	protected EndpointStatus status = EndpointStatus.UNTESTED;


	public AbstractDatasource() {
		super();
	 }


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EndpointFormat getFormat() {
		return format;
	}

	public void setFormat(EndpointFormat format) {
		this.format = format;
	}

	public EndpointStatus getStatus() {
		return status;
	}

	public void setStatus(EndpointStatus status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public String setId(String id) {
		return this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		AbstractDatasource other = (AbstractDatasource) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return DAOService.toJson(this);
	}

	public JsonObject toJson() {
		return GSON.fromJson(this.toString(), JsonObject.class);
	}

}
