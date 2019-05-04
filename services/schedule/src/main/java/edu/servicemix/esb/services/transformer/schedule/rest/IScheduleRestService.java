package edu.servicemix.esb.services.transformer.schedule.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.servicemix.esb.commons.rest.IDefaultRestService;
import edu.servicemix.esb.commons.rest.IPingService;
import edu.servicemix.esb.commons.schedule.GetEventsRequest;

@Path("/")
public interface IScheduleRestService extends IPingService, IDefaultRestService {

	String REQUEST_ENDPOINT = "sedaScheduleAPI";

	@GET
	@Produces(MediaType.APPLICATION_XML)
	Response schedule();
	
	@POST
	@Path("/GetEvents")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	Response getEvents(GetEventsRequest body);
}