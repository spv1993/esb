package edu.servicemix.esb.commons.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface IPingService {

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_XML)
    default String ping() {
        return "<BaseResponse><Status>SUCCESS</Status></BaseResponse>";
    }
}
