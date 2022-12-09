package publisher.rest.model.renderers;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import publisher.rest.exception.RenderTemplateException;

@JsonDeserialize(as=AbstractRenderer.class)
public interface ViewRenderer {
    void setTemplateDirectory(String templateDirectory);
    String getTemplatesDir();
	String render(Object object, String template) throws RenderTemplateException;
	String render(Map<String, Object> model, String template) throws RenderTemplateException;
	Map<String, Object> toModel(Object object);
	Map<String, Object> toModel(List<?> object, String variable);
	String getId();

}
