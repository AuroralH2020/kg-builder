package publisher.rest.model.renderers;


import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

import freemarker.template.Configuration;
import freemarker.template.Version;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import publisher.rest.exception.InternalServiceException;
import publisher.rest.exception.RenderTemplateException;
import publisher.rest.service.DAOService;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonTypeName("FreemarkerRenderer")
public class FreemarkerRenderer extends AbstractRenderer {

	@Transient
	@JsonIgnore
	private FreeMarkerEngine engine;


	public FreemarkerRenderer(String templateDir) {
		super(templateDir);
		initEngine();
		this.id="1";
	}
	public FreemarkerRenderer() {
		super();
		initEngine();
		this.id="1";
	}

	private void initEngine() {
		Configuration configuration = new Configuration(new Version(2, 3, 0));
		try {
			configuration.setDirectoryForTemplateLoading(this.viewsDirectory);
			engine = new FreeMarkerEngine(configuration);
		} catch (IOException e) {
			throw new InternalServiceException(e.getMessage());
		}
	}

	@Override
	public String render(Map<String,Object> model, String template) throws RenderTemplateException {
		String result = null;
		try{
			if(!fileExists(template))
				throw new RenderTemplateException(DAOService.concat("Template ",template," is not located under the directory ",templatesDir));

			result = engine.render(new ModelAndView(model, template));
		}catch(Exception e) {
			System.out.println(e.getMessage());
			throw new RenderTemplateException(e.getMessage());
		}
		return result;
	}

	@Override
	public String render(Object object, String template) throws RenderTemplateException {
		return render(toModel(object), template);
	}


}
