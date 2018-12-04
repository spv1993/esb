package edu.servicemix.esb.commons.rest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import java.util.Map;

class CamelRouteInvoker<T, U> {
    private T request;
    private U response;
    private Class<U> responseClass;
    private Map<String, Object> headers;

    CamelRouteInvoker(T request, Map<String, Object> headers, Class<U> responseClass) {
        this.request = request;
        this.headers = headers;
        this.responseClass = responseClass;
    }

    U getResponse() {
        return response;
    }

    Map<String, Object> getHeaders() {
        return headers;
    }

    void invokeCamelRoute(ProducerTemplate template, String routeURI) throws Exception {
        try {
            Exchange exchange = template.request(routeURI, new InvokeCamelProcessor());
            Exception exception = exchange.getException();
            if (exception != null) {
                throw exception;
            }
            headers = exchange.getOut().getHeaders();
            response = exchange.getOut().getBody(responseClass);
        } catch (Throwable ex) {
            throw new Exception("InvokeCamelRoute throws exception: " + ex.getMessage(), ex);
        }
    }

    private class InvokeCamelProcessor implements Processor {
        @Override
        public void process(Exchange exchange) {
            exchange.getIn().setBody(request);
            exchange.getIn().setHeaders(headers);
        }
    }
}