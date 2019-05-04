package edu.servicemix.esb.services.transformer.manager.rest;

import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class ManagerRestService implements IManagerRestService {
    private static final Logger logger = Logger.getLogger(ManagerRestService.class);

    private ProducerTemplate producerTemplate;

    public ManagerRestService() {
        super();
    }

    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Response doCall(String eventId, String operationName) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", operationName);
        headers.put("eventId", eventId);
        return tryExecute(REQUEST_ENDPOINT, null, headers, producerTemplate, Object.class, logger);
    }

    @Override
    public Response asyncHashGeneration(String eventId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", "hashGeneration");
        headers.put("SyncFlag", "false");
        headers.put("eventId", eventId);
        return tryExecute(REQUEST_ENDPOINT, null, headers, producerTemplate, Object.class, logger);
    }

    @Override
    public Response syncHashGeneration(String eventId) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", "hashGeneration");
        headers.put("SyncFlag", "true");
        headers.put("eventId", eventId);
        return tryExecute(REQUEST_ENDPOINT, null, headers, producerTemplate, Object.class, logger);
    }


    @Override
    public Response getHashes() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("operationName", "getHashes");
        return tryExecute(REQUEST_ENDPOINT, null, headers, producerTemplate, Object.class, logger);
    }
}