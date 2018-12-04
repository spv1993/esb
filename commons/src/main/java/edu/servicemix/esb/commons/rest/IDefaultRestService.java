package edu.servicemix.esb.commons.rest;

import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import javax.ws.rs.core.Response;
import java.util.Map;

public interface IDefaultRestService {

    default <T, U> Response execute(String routeURI, T request, Map<String, Object> headers,
                                 ProducerTemplate template, Class<U> responseClass) throws Exception {

        CamelRouteInvoker<T, U> routeInvoker = new CamelRouteInvoker<>(request, headers, responseClass);
        routeInvoker.invokeCamelRoute(template, routeURI);
        U responseEntity = routeInvoker.getResponse();
        Map<String, Object> headersOut = routeInvoker.getHeaders();

        if (responseEntity == null) {
            return Response.status(406).entity("response Entity is NULL!").build();
        } else if (headersOut.get("CamelHttpResponseCode") == null) {
            return Response.status(200).entity(responseEntity).build();
        } else {
            return Response.status((int) headersOut.get("CamelHttpResponseCode")).build();
        }
    }

    default <T, U> Response tryExecute(String routeURI, T request, Map<String, Object> headers,
                                    ProducerTemplate template, Class<U> responseClass, Logger logger) {
        try {
            return execute(routeURI, request, headers, template, responseClass);
        } catch (Throwable th) {
            logger.error(String.format("ScheduleRestService.%s.invocation error: ", headers.get("operationName")), th);
            throw new RuntimeException(th);
        }
    }
}