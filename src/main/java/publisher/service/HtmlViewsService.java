package publisher.service;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.exception.RepositoryException;
import publisher.rest.model.HtmlView;
import publisher.rest.model.renderers.AbstractRenderer;

public class HtmlViewsService extends CRUDService<HtmlView>{

	protected ViewRendererService service = new ViewRendererService();
	protected RouteService routeService = new RouteService();


	public HtmlViewsService() {
		super(HtmlView.class);
	}


	public boolean update(String id, HtmlView view, String rendererId) {
		AbstractRenderer renderer = service.get(rendererId);
		view.setRender(renderer);
		if(!view.existTemplate())
			throw new InvalidRequestException("Specified html template file ("+view.getTemplate()+") does not exist");
		return this.update(view.getId(), view);
	}

	@Override
	public void delete(String id) {
		routeService.getAll().parallelStream()
			.filter(route -> route.getView()!=null)
			.forEach(route -> {
				System.out.println("Potential to delete: "+route);
				if(route.getView().getId().equals(id)) {
					route.setView(null);
					routeService.update(route.getId(), route);
				}
			});
		routeService.getAll().parallelStream().forEach(elem -> System.out.println(elem));
		try {
			repository.delete(id);
		} catch (RepositoryException e) {
			throw new InvalidRequestException(e.getMessage());
		}
	}

}
