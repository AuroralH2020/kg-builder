package publisher.rest.model.renderers;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;
import publisher.rest.service.DAOService;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({
		@Type(value=VelocityRenderer.class, name= "VelocityRenderer"),
	    @Type(value=FreemarkerRenderer.class, name= "FreemarkerRenderer")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractRenderer implements ViewRenderer{

	@Id
	@Column(name="renderId")
	@JsonProperty
	protected String id;

	@JsonIgnore
	@Transient
	protected File viewsDirectory;
	@JsonProperty
	protected String templatesDir = "./views";

	@JsonIgnore
	@Transient
	protected ObjectMapper mapper;

	@JsonIgnore
	@Transient
	public static final Gson GSON = new Gson();

	public AbstractRenderer(String templatesDir) {
		this.templatesDir = templatesDir;
		mapper = new ObjectMapper();
		viewsDirectory = new File(templatesDir);
		if(!viewsDirectory.exists())
			viewsDirectory.mkdirs();
	}

	public AbstractRenderer() {
		mapper = new ObjectMapper();
		viewsDirectory = new File(templatesDir);
	}

	protected boolean fileExists(String name) {
		return Arrays.asList(viewsDirectory.listFiles()).parallelStream().anyMatch(file -> file.getName().equals(name));
	}

	@Override
	public Map<String, Object> toModel(Object object) {
		@SuppressWarnings("unchecked")
		Map<String, Object> model = mapper.convertValue(object, Map.class);
		return model;
	}



	@Override
	public Map<String, Object> toModel(List<?> object, String variable) {
		Map<String,Object> model = new HashMap<>();
		List<Object> values = object.parallelStream().map(elem -> toModel(elem)).collect(Collectors.toList());
		model.put(variable, values);
		return model;
	}

	@Override
	public void setTemplateDirectory(String templateDirectory) {
		this.templatesDir = templateDirectory;
	}


	@Override
	public String getTemplatesDir() {
		return templatesDir;
	}

	public void setTemplatesDir(String templatesDir) {
		this.templatesDir = templatesDir;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@JsonIgnore
	public void setId(String id) {
		this.id = id;
	}

	public static AbstractRenderer create(JsonObject json) {
		String type = validateField(json, "@type");
		String file = validateField(json, "templatesDir");
		if(type.equals("VelocityRenderer")) {
			return new VelocityRenderer(file);
		}else if(type.equals("FreemarkerRenderer")) {
			return new FreemarkerRenderer(file);
		}else {
			throw new IllegalArgumentException("A non existing renderer @type was provided");
		}
	}

	private static String validateField(JsonObject json, String key) {
		if(!json.has(key))
			throw new IllegalArgumentException("Provided renderer misses mandatory key '"+key+"'");
		return json.get(key).getAsString();
	}

	@Override
	public String toString() {
		return DAOService.toJson(this);
	}


	public JsonObject toJson() {
		return GSON.fromJson(this.toString(), JsonObject.class);
	}

}
