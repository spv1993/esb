package edu.servicemix.esb.services.manager.rest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class ManagerRestService implements IManagerRestService {

    ProducerTemplate producerTemplate;

    public ManagerRestService() {
        super();
    }


    public ProducerTemplate getProducerTemplate() {
        return producerTemplate;
    }

    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Response doCall(String eventId, String operationName) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", operationName);
        headers.put("eventId", eventId);
        return executeOperation(headers, null);
    }

    private Response executeOperation(Map<String, Object> headers, Object body) {
        Exchange exchange = producerTemplate.request(REQUEST_ENDPOINT, new DefaultProcessor(headers, body));
        String responseEntity = exchange.getOut().getBody(String.class);
        Exception exception = exchange.getException();
        if (exception != null) {
            Response.ok(exception.getLocalizedMessage()).build();
        }
        return Response.ok(responseEntity).build();
    }

    private class DefaultProcessor implements Processor {
        private Map<String, Object> headers;
        private Object body;

        DefaultProcessor(Map<String, Object> headers, Object body) {
            this.headers = headers;
            this.body = body;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            exchange.getIn().setBody(body);
            exchange.getIn().setHeaders(headers);
        }
    }
}
