package edu.servicemix.esb.services.transformer.manager.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.servicemix.esb.commons.rest.IDefaultRestService;
import edu.servicemix.esb.commons.rest.IPingService;

public interface IManagerRestService extends IPingService, IDefaultRestService {

    String REQUEST_ENDPOINT = "sedaManagerAPI";

    @GET
    @Produces(MediaType.APPLICATION_XML)
    Response doCall(
            @QueryParam(value = "eventId") String eventId,
            @QueryParam(value = "operationName") String operationName);

    @GET
    @Path("/sync/hashes/generation")
    @Produces(MediaType.APPLICATION_XML)
    Response syncHashGeneration(
            @QueryParam(value = "eventId") String eventId);

    @GET
    @Path("/async/hashes/generation")
    @Produces(MediaType.APPLICATION_XML)
    Response asyncHashGeneration(
            @QueryParam(value = "eventId") String eventId);

    @GET
    @Path("/hashes")
    @Produces(MediaType.APPLICATION_XML)
    Response getHashes();
}