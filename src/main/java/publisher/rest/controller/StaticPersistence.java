package publisher.rest.controller;

import publisher.rest.model.HtmlView;
import publisher.rest.model.PublisherRoute;
import publisher.rest.model.endpoint.AbstractEndpoint;
import publisher.rest.model.endpoint.EndpointFactory;
import publisher.rest.model.endpoint.EndpointFormat;
import publisher.rest.model.endpoint.aggregated.AggregatedRDF;
import publisher.rest.model.endpoint.aggregated.JsonWrapper;
import publisher.rest.model.endpoint.aggregated.SparqlOverLinks;
import publisher.rest.model.renderers.AbstractRenderer;
import publisher.service.DAOService;

public class StaticPersistence {

	public static  void updateMockData(){
		//EndpointController.repository.retrieve().parallelStream().forEach(elem -> EndpointController.repository.delete(elem.getId()));
		//DomainController.repository.retrieve().parallelStream().forEach(elem -> EndpointController.repository.delete(elem.getId()));

//		EndpointController.repository.retrieve().parallelStream().forEach(elem -> EndpointController.repository.delete(elem.getId()));
//		EndpointController.repository2.retrieve().parallelStream().forEach(elem -> EndpointController.repository.delete(elem.getDomain()));

		AbstractEndpoint e0 = (AbstractEndpoint) EndpointFactory.createEndpoint("Person CSV", EndpointFormat.CSV,
				"https://raw.githubusercontent.com/helio-ecosystem/helio-ecosystem/main/resources/mappings-data/ldac/people.csv");
		e0.setId("e0");
		AbstractEndpoint e1 = (AbstractEndpoint) EndpointFactory.createEndpoint("Madrid Dbpedia", EndpointFormat.RDFXML,
				"https://es.dbpedia.org/data/Madrid.xml");
		e1.setId("e1");
		AbstractEndpoint e2 = (AbstractEndpoint) EndpointFactory.createEndpoint("Test SPARQL", EndpointFormat.JSON, "SELECT * {?S ?P ?O .} LIMIT 10",
				"https://dbpedia.org/sparql", null, null, null);
		e2.setId("e2");
		AbstractEndpoint e3 = (AbstractEndpoint) EndpointFactory.createEndpoint("Tripletstore", EndpointFormat.TURTLE, null,
				"http://localhost:7200/repositories/testhelio", null, null, "http://tutorial.odrl.org");

		e3.setId("e3");
		AbstractEndpoint e4 = (AbstractEndpoint) EndpointFactory.createEndpoint("JSON-LD 1.1 test", EndpointFormat.JSONLD,
				"https://raw.githubusercontent.com/AuroralH2020/auroral-data-payloads-examples/main/tourism/payload1.json");
		e4.setId("e4");

		System.out.println(e0);
		if (!EndpointController.service.getOptional(e0.getId()).isPresent())
			EndpointController.service.update(e0);
		if (!EndpointController.service.getOptional(e1.getId()).isPresent())
			EndpointController.service.update(e1);
		if (!EndpointController.service.getOptional(e2.getId()).isPresent())
			EndpointController.service.update(e2);
		if (!EndpointController.service.getOptional(e3.getId()).isPresent())
			EndpointController.service.update(e3);
		if (!EndpointController.service.getOptional(e4.getId()).isPresent())
			EndpointController.service.update(e4);


		JsonWrapper ave = new  JsonWrapper();
		ave.setId("ave0");
		String id = ave.getId();
		ave.addEndpoint(e0);
		ave.addEndpoint(e1);
		ave.setName("AVE");
		System.out.println(DAOService.toJson(ave));

		if(!AggregatedEndpointController.service.exist(ave.getId()))
			AggregatedEndpointController.service.update(ave);

		AggregatedRDF ave1 = new  AggregatedRDF();
		ave1.setId("ave1");
		ave1.addEndpoint(e4);
		ave1.setName("RDF ave");
		ave1.setFormat(EndpointFormat.TURTLE);

		System.out.println(DAOService.toJson(ave1));

		if(!AggregatedEndpointController.service.exist(ave1.getId()))
			AggregatedEndpointController.service.update(ave1);



		ave =null;
		ave = AggregatedEndpointController.service.getOptional(id).get();
		System.out.println(DAOService.toJson(ave));
		System.out.println("..........");


		AbstractRenderer renderer = ViewRendererController.service.get("1");
		System.out.println(renderer);
		HtmlView view1 = new HtmlView();
		view1.setId("vH0");
		view1.setTemplate("./test.html");
		view1.setRender(renderer);
		if(!HtmlViewsController.service.exist("vH0"))
			HtmlViewsController.service.update(view1);
		System.out.println(view1);


		PublisherRoute vdomain = new PublisherRoute();
		vdomain.setId("vd1");
		vdomain.setRoute("/opt1");
		vdomain.setEndpoint(ave);
		vdomain.setIsRegex(false);
		vdomain.setView(view1);
		PublisherRoute vdomain2 = new PublisherRoute();
		vdomain2.setId("vd2");
		vdomain2.setRoute("/policy\\d+");
		vdomain2.setEndpoint(e3);
		vdomain2.setIsRegex(true);

		if(!RouteController.service.exist(vdomain.getId()))
			RouteController.service.update(vdomain);
		if(!RouteController.service.exist(vdomain2.getId()))
			RouteController.service.update(vdomain2);
		vdomain =RouteController.service.get("vd1");

		vdomain2 =RouteController.service.get("vd2");
		System.out.println(DAOService.toJson(vdomain));
		System.out.println(DAOService.toJson(vdomain2));

		
		SparqlOverLinks s4l = new  SparqlOverLinks();
		s4l.addEndpoint(e4);
		s4l.setId("s4L0");
		s4l.setName("Test");
		if(!AggregatedEndpointController.service.exist(s4l.getId()))
			AggregatedEndpointController.service.update(s4l);
		s4l.addEndpoint(e1);
		AggregatedEndpointController.service.update("s4L0", s4l);
	}
}
