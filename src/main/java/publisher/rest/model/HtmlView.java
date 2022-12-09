package publisher.rest.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import publisher.rest.model.renderers.AbstractRenderer;
import publisher.service.DAOService;


@Entity
@JsonInclude(Include.NON_NULL)
public class HtmlView {



	@Id
	@Column(name="viewId")
	protected String id = UUID.randomUUID().toString();

	@NotBlank
	protected String template;

	@NotNull
	@OneToOne(targetEntity= AbstractRenderer.class, fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name="renderId")
	protected AbstractRenderer render;

	@JsonIgnore
	@Transient
	public static final Gson GSON = new Gson();



	public HtmlView() {
		super();
	}


	public boolean existTemplate() {
		boolean result = isInSubDirectory(new File(template));
		System.out.println("final result: "+result);
		return result;
	}

	public boolean isInSubDirectory( File file) {
//		final Path dir = Paths.get(this.getRender().getTemplatesDir()).toAbsolutePath();
//        final Path file2 = Paths.get(template).toAbsolutePath();
//        System.out.println("Dir: " + dir);
//        System.out.println("File: " + file2);
//        final boolean valid = file2.startsWith(dir);
//        System.out.println("Valid: " + valid);
		// TODO: THE FILE MUST BE SEARCHED IN THE WHOLE DIRECTORY TREE STRUCTURE (ALSO SUB-DIRECTORIES)
		try (Stream<Path> walkStream = Files.walk(Paths.get(this.getRender().getTemplatesDir()))) {
		    return walkStream.filter(p -> p.toFile().isFile()).anyMatch(f -> f.endsWith(file.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return false;
	}

	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public AbstractRenderer getRender() {
		return render;
	}

	public void setRender(AbstractRenderer render) {
		this.render = render;
	}

	public JsonObject toJson() {
		return GSON.fromJson(this.toString(), JsonObject.class);
	}

	@Override
	public String toString() {
		return DAOService.toJson(this);
	}
}
