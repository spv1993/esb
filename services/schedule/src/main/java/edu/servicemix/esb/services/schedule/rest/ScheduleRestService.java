package edu.servicemix.esb.services.schedule.rest;

import javax.ws.rs.core.Response;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

import edu.servicemix.esb.commons.schedule.GetEventsRequest;
import edu.servicemix.esb.commons.schedule.ScheduleResponse;

public class ScheduleRestService implements IScheduleRestService {

	ProducerTemplate producerTemplate;
	
	public ScheduleRestService() {	
		super();
	}

	public ProducerTemplate getProducerTemplate() {
		return producerTemplate;
	}

	public void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	@Override
	public Response schedule() {
		return executeOperation("schedule", null);
	}

	@Override
	public Response getEvents(GetEventsRequest body) {
		return executeOperation("GetEvents", body);
	}
	
	private ScheduleResponse buildErrorScheduleResponse(String errorMessage) {
		ScheduleResponse response = new ScheduleResponse();
		response.setCode("-1");
		response.setStatus(errorMessage);
		return response;
	}
	
	private Response executeOperation(String operationName, Object body) {
		Exchange exchange = producerTemplate.request(REQUEST_ENDPOINT, new DefaultProcessor(operationName, body));
		ScheduleResponse responseEntity = exchange.getOut().getBody(ScheduleResponse.class);
		System.out.println(responseEntity);

		if (exchange.getException() != null || responseEntity == null) {
			String errorMessage = exchange.getException().getMessage();
			responseEntity = buildErrorScheduleResponse(errorMessage);
		}
		
		return Response.ok(responseEntity).build();
	}
	
	private class DefaultProcessor implements Processor {
		private String operationName;
		private Object body;
		
		public DefaultProcessor(String operationName, Object body) {
			this.operationName = operationName;
			this.body = body;
		}
		
		@Override
		public void process(Exchange exchange) throws Exception {
			exchange.getIn().setBody(body);
			exchange.getIn().setHeader("operationName", operationName);
		}
	}
}
