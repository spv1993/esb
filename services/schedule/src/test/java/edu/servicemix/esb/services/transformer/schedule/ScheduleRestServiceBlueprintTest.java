package edu.servicemix.esb.services.transformer.schedule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScheduleRestServiceBlueprintTest extends CamelBlueprintTestSupport {

    private static final String SCHEDULE_RESPONSE_FILENAME = "src/test/resources/ScheduleResponse.xml";
	private static final String BASE_URL = "http://localhost:10001/cxf/rest/schedule";
    private static CloseableHttpClient httpClient;
    
    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/amq-context.xml,OSGI-INF/blueprint/context.xml";
    }

    @Override
    protected String useOverridePropertiesWithConfigAdmin(Dictionary<String, String> props) throws Exception {
        props.put("rs.address", BASE_URL);
        return "edu.servicemix.esb.services.schedule";
    }
    
    @Override
    public String isMockEndpointsAndSkip() {
        return ".*activemq:.*";
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
		return new TetRouteBuilder();
    };
    
    @Override
    public void setUp() throws Exception {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
        super.setUp();
    }

    @After
    public void closeHttpClient() throws IOException {
        if (httpClient != null) {
        	httpClient.close();
            httpClient = null;
        }
    }
    
//    @Test
    public void testScheduleOperation() throws Exception {
        System.out.println("TestScheduleOperation is executing...");
        String stringResponse = executeGetRequest();
        System.out.println(stringResponse);
    }
    
    @Test
    public void testGetEventsOperation() throws Exception {
        System.out.println("TestGetEventsOperation is executing...");
        
        String requestString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><GetEventsRequest xmlns=\"www.servicemix.edu/schedule/data\"><user>pavel</user><from>2018-10-13T00:00:00</from></GetEventsRequest>"; 
        String operationUrl = BASE_URL + "/GetEvents";
        
        HttpPost httpPost = new HttpPost(operationUrl);
        httpPost.setEntity(new StringEntity(requestString));
        httpPost.setHeader("Accept", "application/xml");
        httpPost.setHeader("Content-type", "application/xml");
        
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String stringResponse = EntityUtils.toString(response.getEntity());
        
        System.out.println(stringResponse);
    }

    private String executeGetRequest() throws Exception {
    	CloseableHttpResponse response = httpClient.execute(new HttpGet(BASE_URL));
    	return EntityUtils.toString(response.getEntity());
    }
    
    private String getPayload() throws Exception {
    	return new String(Files.readAllBytes(Paths.get(SCHEDULE_RESPONSE_FILENAME)));
    }
    
    private class TetRouteBuilder extends RouteBuilder {
		@Override
		public void configure() throws Exception {
			context.getRouteDefinition("amq-sender-schedule-rs").adviceWith(context, new AdviceWithRouteBuilder() {
				@Override
				public void configure() throws Exception {
					weaveByToString(".*activemq:.*").replace().process(exchange -> {
						exchange.getOut().setBody(getPayload());
						exchange.getOut().setHeaders(exchange.getIn().getHeaders());
					});
				}
			});
		}
	}
}
