package publisher.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SystemConfig {

	@Id
	public String id = "SystemConfig";

	public String staticFilesDirectory = "./static";

	public SystemConfig() {
		super();
	}


	public String getId() {
		return id;
	}

	@JsonIgnore
	public void setId(String id) {
		this.id = id;
	}

	public String getStaticFilesDirectory() {
		return staticFilesDirectory;
	}

	public void setStaticFilesDirectory(String staticFilesDirectory) {
		this.staticFilesDirectory = staticFilesDirectory;
	}




}
