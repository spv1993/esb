package edu.servicemix.esb.datasources.test;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.camel.util.KeyValueHolder;
import org.junit.Test;

import java.util.Dictionary;
import java.util.Map;

public class DSEventsTest extends CamelBlueprintTestSupport {

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/context.xml";
    }

    @Override
    protected void addServicesOnStartup(Map<String, KeyValueHolder<Object, Dictionary>> services) {
        services.put("org.apache.camel.CamelContext", asService(new DefaultCamelContext(), null));
    }

    @Test
    public void testContextIsValid() {
    }
}
