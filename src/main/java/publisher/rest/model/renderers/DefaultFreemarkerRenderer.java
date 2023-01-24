package publisher.rest.model.renderers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Version;
import publisher.rest.exception.RenderTemplateException;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class DefaultFreemarkerRenderer {
	private FreeMarkerEngine engine;
	protected ObjectMapper mapper;




	public DefaultFreemarkerRenderer() {
        Configuration configuration = new Configuration(new Version(2, 3, 23));
		engine = new FreeMarkerEngine();

		mapper = new ObjectMapper();
	}

	public String render(Map<String,Object> model, String template) throws RenderTemplateException {
		String result = null;
		try{
			result = engine.render(new ModelAndView(model, template));
		}catch(Exception e) {
			System.out.println(e.getMessage());
			throw new RenderTemplateException(e.getMessage());
		}
		return result;
	}


	public String render(Object object, String template) throws RenderTemplateException {
		return render(toModel(object), template);
	}


	public Map<String, Object> toModel(Object object) {
		@SuppressWarnings("unchecked")
		Map<String, Object> model = mapper.convertValue(object, Map.class);
		return model;
	}




	public Map<String, Object> toModel(List<?> object, String variable) {
		Map<String,Object> model = new HashMap<>();
		List<Object> values = object.parallelStream().map(elem -> toModel(elem)).collect(Collectors.toList());
		model.put(variable, values);
		return model;
	}
}
