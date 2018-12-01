package edu.servicemix.esb.services.manager.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface IManagerRestService {

    String REQUEST_ENDPOINT = "sedaManagerAPI";

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_XML)
    default String ping() {
        return "<BaseResponse><Status>SUCCESS</Status></BaseResponse>";
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    Response doCall(
            @QueryParam(value = "eventId") String eventId,
            @QueryParam(value = "operationName") String operationName);
}
