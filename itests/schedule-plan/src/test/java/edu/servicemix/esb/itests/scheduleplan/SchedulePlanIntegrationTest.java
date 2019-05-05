package edu.servicemix.esb.itests.scheduleplan;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import edu.servicemix.esb.adapters.events.mybatis.EventAggregationStrategy;
import edu.servicemix.esb.adapters.events.mybatis.typehandlers.DurationTypeHandler;
import edu.servicemix.esb.commons.schedule.Event;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
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
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SchedulePlanIntegrationTest extends CamelTestSupport {
    private transient Logger log = LoggerFactory.getLogger(getClass());

    private static final String KARAF_VERSION = "3.0.8";
    private static final String PROJECT_VERSION = "1.0.0";
    private static final String CAMEL_VERSION = "2.16.5";
    private static final String CXF_VERSION = "3.1.9";

    private static int cxfPort = 8181;
    private static int sshPort = 8101;
    private static int amqPort = 61616;
    private static int rmiServerPort = 44444;
    private static int rmiRegistryPort = 1101;

    @Inject
    protected FeaturesService featuresService;

    @Inject
    protected BundleContext bundleContext;

//    @Inject
//    @Filter(value="(camel.context.name=events-context)", timeout=10000)
//    protected CamelContext eventsContext;

    @Configuration
    public static Option[] configure() throws Exception {
        rmiRegistryPort = AvailablePortFinder.available(rmiRegistryPort) ? rmiRegistryPort : AvailablePortFinder.getNextAvailable(rmiRegistryPort);
        rmiServerPort = AvailablePortFinder.available(rmiServerPort) ? rmiServerPort : AvailablePortFinder.getNextAvailable(rmiServerPort);
        cxfPort = AvailablePortFinder.available(cxfPort) ? cxfPort : AvailablePortFinder.getNextAvailable(cxfPort);
        amqPort = AvailablePortFinder.available(amqPort) ? amqPort : AvailablePortFinder.getNextAvailable(amqPort);
        sshPort = AvailablePortFinder.available(sshPort) ? sshPort : AvailablePortFinder.getNextAvailable(sshPort);


        MavenUrlReference camUrl = maven().groupId("org.apache.camel.karaf").artifactId("apache-camel").type("xml").classifier("features").version(CAMEL_VERSION);
        MavenUrlReference cxfUrl = maven().groupId("org.apache.cxf.karaf").artifactId("apache-cxf").type("xml").classifier("features").version(CXF_VERSION);

        MavenUrlReference entUrl = maven().groupId("org.apache.karaf.features").artifactId("enterprise").type("xml").classifier("features").version(KARAF_VERSION);
        MavenUrlReference jdbcUrl = maven().groupId("org.ops4j.pax.jdbc").artifactId("pax-jdbc-features").classifier("features").type("xml").version("0.7.0");
        MavenUrlReference amqUrl = maven().groupId("org.apache.activemq").artifactId("activemq-karaf").type("xml").classifier("features").version("5.12.3");

        return new Option[] {
                createKarafDistributionConfiguration(),

                logLevel(LogLevel.INFO),
                keepRuntimeFolder(),
                junitBundles(),

                editConfigurationFilePut("etc/org.apache.karaf.shell.cfg", "sshPort", String.valueOf(sshPort)),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiRegistryPort", String.valueOf(rmiRegistryPort)),
                editConfigurationFilePut("etc/org.apache.karaf.management.cfg", "rmiServerPort", String.valueOf(rmiServerPort)),
                editConfigurationFilePut("etc/org.ops4j.pax.web.cfg", "org.osgi.service.http.port", String.valueOf(cxfPort)),
                editConfigurationFilePut("etc/system.properties", "org.osgi.service.http.port", String.valueOf(cxfPort)),
                editConfigurationFilePut("etc/system.properties", "activemq.port", String.valueOf(amqPort)),

                features(entUrl, "jndi"),
                features(amqUrl, "activemq-blueprint", "activemq-camel"),
                features(camUrl, "camel", "camel-test", "camel-cxf", "camel-jaxb", "camel-groovy",
                        "camel-jms", "camel-mybatis", "camel-saxon"),

                mavenBundle("org.apache.cxf.xjc-utils", "cxf-xjc-runtime", "3.1.0"),
                mavenBundle("org.apache.tomcat", "tomcat-jdbc", "9.0.2"),
                mavenBundle("org.postgresql", "postgresql", "9.4.1212"),

                createCommonsBundle(),
                createEventsDataSourceBundle(),
                createEventsAdapterBundle(),
                createScheduleServiceBundle(),
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
    }

    @After
    public void tearDownTest() {
        log.info("------------------------------- Test is finished --------------------------------");
    }

    @Test
    public void testScheduleRoute() throws Exception {
        log.info("'ScheduleITest' is running...");
        Client client = ClientBuilder.newClient();
        WebTarget wt = client.target("http://localhost:8182/cxf/rest/schedule");
        String response = wt.request().get(String.class);

        System.out.println("RESPONSE: " + response);

        log.info("'ScheduleITest' is finished.");
    }

    public static Option createEventsAdapterBundle() throws FileNotFoundException {
        InputStream isEventAmqContext = new FileInputStream(new File("../../adapters/events/src/main/resources/OSGI-INF/blueprint/amq-context.xml"));
        InputStream isEventCamContext = new FileInputStream(new File("../../adapters/events/src/main/resources/OSGI-INF/blueprint/context.xml"));
        InputStream isEventSQLMapConf = new FileInputStream(new File("../../adapters/events/src/main/resources/SqlMapConfig.xml"));
        InputStream isEventMyBatisMap = new FileInputStream(new File("../../adapters/events/src/main/resources/META-INF/mybatis/events-mapper.xml"));
        InputStream isEventXSLErrResp = new FileInputStream(new File("../../adapters/events/src/main/resources/META-INF/xsl/Dummy2ScheduleSuccessResponse.xsl"));
        InputStream isEventXSLSucResp = new FileInputStream(new File("../../adapters/events/src/main/resources/META-INF/xsl/ScheduleResponse.xsl"));
        return streamBundle(
                bundle().add(DurationTypeHandler.class)
                        .add(EventAggregationStrategy.class)
                        .add("OSGI-INF/blueprint/amq-context.xml", isEventAmqContext)
                        .add("OSGI-INF/blueprint/context.xml", isEventCamContext)
                        .add("SqlMapConfig.xml", isEventSQLMapConf)
                        .add("META-INF/mybatis/events-mapper.xml", isEventMyBatisMap)
                        .add("META-INF/xsl/Dummy2ScheduleSuccessResponse.xsl", isEventXSLErrResp)
                        .add("META-INF/xsl/ScheduleResponse.xsl", isEventXSLSucResp)
                        .set(Constants.BUNDLE_SYMBOLICNAME, "edu.servicemix.esb.adapters.events")
                        .set(Constants.BUNDLE_VERSION, PROJECT_VERSION)
                        .set(Constants.DYNAMICIMPORT_PACKAGE, "*").build()).start();
    }

    public static Option createCommonsBundle() {
        return mavenBundle().groupId("edu.servicemix.esb").artifactId("commons").version(PROJECT_VERSION);
    }

    public static Option createEventsDataSourceBundle() {
        return mavenBundle().groupId("edu.servicemix.esb.datasources").artifactId("ds-events").version(PROJECT_VERSION);
    }

    public static Option createScheduleServiceBundle() {
        return mavenBundle().groupId("edu.servicemix.esb.services").artifactId("schedule").version(PROJECT_VERSION);
    }

    public static Option createKarafDistributionConfiguration() {
        MavenUrlReference karUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").version(KARAF_VERSION);
        return karafDistributionConfiguration()
                .frameworkUrl(karUrl)
                .useDeployFolder(false)
                .karafVersion(KARAF_VERSION)
                .unpackDirectory(new File("target/paxexam/unpack/"));
    }
}
