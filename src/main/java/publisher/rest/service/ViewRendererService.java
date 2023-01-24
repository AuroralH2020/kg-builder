package publisher.rest.service;

import publisher.rest.model.SystemConfig;
import publisher.rest.model.renderers.AbstractRenderer;
import publisher.rest.model.renderers.FreemarkerRenderer;
import publisher.rest.model.renderers.VelocityRenderer;
import publisher.rest.persistence.Repository;

public class ViewRendererService extends CRUDService<AbstractRenderer>{

	private Repository<SystemConfig> repositoryConfig = new Repository<>(SystemConfig.class);

	public ViewRendererService() {
		super(AbstractRenderer.class);
	}

	public void init()  {
		if(this.repository.retrieve().isEmpty()) {
			System.out.println("Initialising renders");
			AbstractRenderer renderer1 = new FreemarkerRenderer();
			AbstractRenderer renderer2 = new VelocityRenderer();
			repository.persist(renderer1);
			repository.persist(renderer2);
		}
		if(repositoryConfig.retrieve().isEmpty()) {
			System.out.println("Initialising system config");
			SystemConfig config = new SystemConfig();
			repositoryConfig.persist(config);
		}
		System.out.println("done!");
	}

	public SystemConfig getSystemConfig() {
		return this.repositoryConfig.retrieve().get(0);
	}

	public boolean updateSystemConfig(SystemConfig systemConf) {
		String id = this.repositoryConfig.retrieve().get(0).getId();
		systemConf.setId(id);
		boolean existed = this.repositoryConfig.exists(id);
		if (existed)
			this.repositoryConfig.delete(id);
		this.repositoryConfig.persist(systemConf);

		return existed;
	}
}
