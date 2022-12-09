package publisher.service;

import java.io.IOException;

import com.google.gson.JsonObject;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.model.AbstractDatasource;
import publisher.rest.model.HtmlView;
import publisher.rest.model.PublisherRoute;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;
import publisher.rest.persistence.Repository;

public class RouteService extends CRUDService<PublisherRoute>{

	protected static Repository<PublisherRoute> repository = new Repository<>(PublisherRoute.class);
	protected static CRUDService<AbstractEndpoint> endpointsService = new CRUDService<>(AbstractEndpoint.class);
	protected static CRUDService<JsonWrapper> aggregatedService = new CRUDService<>(JsonWrapper.class);
	protected static CRUDService<HtmlView> viewsService = new CRUDService<>(HtmlView.class);

	public RouteService() {
		super(PublisherRoute.class);
	}

	public PublisherRoute createUpdate(String routeJson) {
		JsonObject route = DAOService.castToJson(routeJson);
		AbstractDatasource endpoint = null;
		HtmlView view = null;
		boolean hasView =  route.has("view");
		boolean hasEndpoint = route.has("endpoint");
		if(!hasView && !hasEndpoint)
			throw new InvalidRequestException("The Route must have at least an HTML view or a data source");

		if(hasView) {
			String viewId = route.remove("view").getAsString();
			if(viewsService.exist(viewId)) {
				view = viewsService.get(viewId);
			}else if(viewId.equals("None")) {
				//skip
			}else{
				throw new InvalidRequestException("The id provided in the Route belongs to no existing endpoint or aggregated endpoint ");
			}
		}
		if(hasEndpoint) {
			String id = route.remove("endpoint").getAsString();
			if(endpointsService.exist(id)) {
				endpoint = endpointsService.get(id);
			}else if (aggregatedService.exist(id)) {
				endpoint = aggregatedService.get(id);
			}else if(id.equals("None")) {
				//skip
			}else{
				throw new InvalidRequestException("The id provided in the Route belongs to no existing endpoint or aggregated endpoint ");
			}
		}
		try {
			PublisherRoute vdomain = (PublisherRoute) DAOService.fromJson(route.toString(), PublisherRoute.class);
			vdomain.setEndpoint(endpoint);
			vdomain.setView(view);
			this.update(vdomain.getId(), vdomain);
			if(!this.exist(vdomain.getId()))
				return vdomain;

		}catch(IOException e) {
			e.printStackTrace();
			throw new InvalidRequestException(e.getMessage());
		}
		return null;
	}


}
