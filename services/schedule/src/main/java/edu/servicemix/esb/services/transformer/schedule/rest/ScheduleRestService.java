package edu.servicemix.esb.services.transformer.schedule.rest;

import javax.ws.rs.core.Response;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
import edu.servicemix.esb.commons.schedule.GetEventsRequest;
import edu.servicemix.esb.commons.schedule.ScheduleResponse;

public class ScheduleRestService implements IScheduleRestService {
	private static final Logger logger = Logger.getLogger(ScheduleRestService.class);

	private ProducerTemplate producerTemplate;
	
	public ScheduleRestService() {	
		super();
	}

	public void setProducerTemplate(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}

	@Override
	public Response schedule() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("operationName", "schedule");
		return tryExecute(REQUEST_ENDPOINT, null, headers,
				producerTemplate, ScheduleResponse.class, logger);
	}

	@Override
	public Response getEvents(GetEventsRequest body) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("operationName", "GetEvents");
		return tryExecute(REQUEST_ENDPOINT, body, headers,
				producerTemplate, ScheduleResponse.class, logger);
	}
}