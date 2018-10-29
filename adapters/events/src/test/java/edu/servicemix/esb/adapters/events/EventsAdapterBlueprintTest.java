package edu.servicemix.esb.adapters.events;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;
import org.apache.cxf.jaxb.DatatypeFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edu.servicemix.esb.commons.schedule.ArrayOfEvents;
import edu.servicemix.esb.commons.schedule.Event;
import edu.servicemix.esb.commons.schedule.ScheduleResponse;

public class EventsAdapterBlueprintTest extends CamelBlueprintTestSupport {

	private static final String INPUT_ENDPOINT = "direct:inputEndpoint";
	
	@Override
	protected String getBlueprintDescriptor() {
		return "/OSGI-INF/blueprint/amq-context.xml,/OSGI-INF/blueprint/context.xml";
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		CamelContext camelContext = super.createCamelContext();		
		camelContext.addComponent("mybatis", new MockComponent());
		return camelContext;
	}
	
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				replaceRouteFromWith("events-amq-reader", INPUT_ENDPOINT);
				
				context.getRouteDefinition("events-operation-schedule").adviceWith(context, new AdviceWithRouteBuilder() {		
					@Override
					public void configure() throws Exception {
						weaveByToString(".*mybatis:.*").replace().process(exchange -> {
							ScheduleResponse response = new ScheduleResponse();
							response.setArrayOfEvents(new ArrayOfEvents());
							
							for (int i = 0; i < 5; i++) {
								response.getArrayOfEvents().getEvent().add(EventFactory.createDummyEvent(i));
							}
							exchange.getOut().setBody(new ArrayList<ScheduleResponse>(Arrays.asList(response)));
							exchange.getOut().setHeaders(exchange.getIn().getHeaders());
						});
					}
				});
			}
		};
	}

	@Test
	public void testRoute() {
		System.out.println("TEST IS STARTING");
		template.sendBodyAndHeader(INPUT_ENDPOINT, "<dummy/>", "operationName", "schedule");
	}

	private static class EventFactory {
		public static Event createDummyEvent(int count) {
			Event event = new Event();
			event.setName("Event Name №" + count);
			event.setDescription("String Description №" + count);
			event.setType("Event Type №" + count);
			event.setUser("pavel");
			event.setStart(new Date());
			event.setDuration(DatatypeFactory.createDuration("PT" + count + "H"));
			return event;
		}
	}
}
