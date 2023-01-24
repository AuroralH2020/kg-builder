package publisher.rest.model.renderers;

import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import publisher.rest.exception.RenderTemplateException;
import publisher.rest.service.DAOService;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeName("VelocityRenderer")
public class VelocityRenderer extends AbstractRenderer {

	@Transient
	@JsonIgnore
	private VelocityTemplateEngine engine;

	public VelocityRenderer(String templateDir) {
		super(templateDir);
		initEngine();
		this.id="2";
	}

	public VelocityRenderer() {
		super();
		initEngine();
		this.id="2";
	}

	private void initEngine() {
		Properties properties = new Properties();
		properties.setProperty("file.resource.loader.path", templatesDir);
		engine = new VelocityTemplateEngine(new VelocityEngine(properties));
	}

	@Override
	public String render(Map<String, Object> model, String template) throws RenderTemplateException {
		try {
			if (!fileExists(template))
				throw new RenderTemplateException(
						DAOService.concat("Template ", template, " is not located under the directory ", templatesDir));

			return engine.render(new ModelAndView(model, template));
		} catch (Exception e) {
			throw new RenderTemplateException(e.getMessage());
		}
	}

	@Override
	public String render(Object object, String template) throws RenderTemplateException {
		return render(toModel(object), template);
	}




}