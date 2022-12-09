package publisher.rest.model.endpoint;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import publisher.rest.exception.EndpointFormatCompatibilityException;
import publisher.rest.model.AbstractDatasource;
import spark.Request;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME)
@JsonSubTypes({ @Type(Link.class), @Type(TriplestoreDescribe.class), @Type(SPARQL.class), @Type(TriplestoreSparql.class)})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractEndpoint extends AbstractDatasource implements Endpoint, TesteableEndpoint  {

	

	
	@Override
	public String testEndpoint(Request request) throws EndpointFormatCompatibilityException{
		try {
			String data = getData(request);
			EndpointFormat.isFormatCompliant(data, format); // throws exeception
			this.status = EndpointStatus.VALID;
			return data;
		}catch(Exception e) {
			this.status = EndpointStatus.INVALID;
			throw new EndpointFormatCompatibilityException(e.getMessage());
		}
	}

	





}
