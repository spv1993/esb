package edu.servicemix.esb.adapters.events.mybatis;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import edu.servicemix.esb.commons.schedule.ArrayOfEvents;
import edu.servicemix.esb.commons.schedule.Event;
import edu.servicemix.esb.commons.schedule.ScheduleResponse;

public class EventAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		Event event = newExchange.getIn().getBody(Event.class);
		
		if (oldExchange == null) {
			ScheduleResponse scheduleResponse = new ScheduleResponse();
			scheduleResponse.setArrayOfEvents(new ArrayOfEvents());
			scheduleResponse.getArrayOfEvents().getEvent().add(event);
			
			newExchange.getIn().setBody(scheduleResponse);
			return newExchange;
		}
		
		ScheduleResponse scheduleResponse = oldExchange.getIn().getBody(ScheduleResponse.class);
		scheduleResponse.getArrayOfEvents().getEvent().add(event);
		return oldExchange;
	}

}
