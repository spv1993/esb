package edu.servicemix.esb.itests.transformation;

import edu.servicemix.esb.services.transformer.Transform;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.karaf.features.FeaturesService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.streamBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;


@RunWith(PaxExam.class)
public class TransformationIntegrationTest extends CamelTestSupport {

    private transient Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    protected FeaturesService featuresService;

    @Inject
    protected BundleContext bundleContext;

    @Inject
    @Filter(value="(camel.context.name=transformerContext)", timeout=10000)
    protected CamelContext transformerContext;

    @Configuration
    public static Option[] configure() throws Exception {
        String rmiRegistryPortForTests = String.valueOf(AvailablePortFinder.getNextAvailable());
        String rmiServerPortForTests = String.valueOf(AvailablePortFinder.getNextAvailable(44444));

        MavenUrlReference karUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").version("3.0.1");
        MavenUrlReference camUrl = maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").type("xml").classifier("features").version("2.16.5");

        InputStream is = new FileInputStream(new File("../../services/transformer/src/main/resources/OSGI-INF/blueprint/context.xml"));

        return new Option[] {
                karafDistributionConfiguration()
                        .frameworkUrl(karUrl)
                        .useDeployFolder(false)
                        .karafVersion("3.0.1")
                        .unpackDirectory(new File("target/paxexam/unpack/")),

                logLevel(LogLevel.INFO),
                keepRuntimeFolder(),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", rmiRegistryPortForTests),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", rmiServerPortForTests),

                features(camUrl, "camel-blueprint", "camel-test"),

                streamBundle(
                        bundle().add(Transform.class)
                                .add("OSGI-INF/blueprint/context.xml", is)
                                .set(Constants.BUNDLE_SYMBOLICNAME, "edu.servicemix.esb.services.transformer")
                                .set(Constants.DYNAMICIMPORT_PACKAGE, "*").build()).start()
        };
    }

    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }

    @Before
    public void setUpTest() throws Exception {
        log.info("-------------------------------- Running test ----------------------------------");
        assertTrue(featuresService.isInstalled(featuresService.getFeature("camel-core")));
        assertTrue(featuresService.isInstalled(featuresService.getFeature("camel-blueprint")));
        assertNotNull(transformerContext);
    }

    @After
    public void tearDownTest() {
        log.info("------------------------------- Test is finished --------------------------------");
    }

    @Test
    public void testTransformTimerRoutes() throws Exception {
        log.info("'TransformTimerRoute' is running...");
        MockEndpoint mockEndpoint = (MockEndpoint) transformerContext.getEndpoint("mock:result");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.assertIsSatisfied(10000);
        log.info("'TransformTimerRoute' is finished.");
    }

    @Test
    public void testTransformPingRoutes() {
        log.info("'TransformPingRoute' is running...");
        ProducerTemplate transformerTemplate = transformerContext.createProducerTemplate();
        String response = transformerTemplate.requestBody("direct:transform-ping-route", null, String.class);
        assertEquals("SUCCESS", response);
        log.info("'TransformPingRoute' is finished. Received body: " + response);
    }

}
