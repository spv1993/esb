package edu.servicemix.esb.services.schedule.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.servicemix.esb.commons.schedule.GetEventsRequest;

@Path("/")
public interface IScheduleRestService {

	String REQUEST_ENDPOINT = "sedaScheduleAPI";
	
	@GET
	@Path("/ping")
	default String ping() {
		return "SUCCESS";
	}

	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response schedule();
	
	@POST
	@Path("/GetEvents")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public Response getEvents(GetEventsRequest body);
}
