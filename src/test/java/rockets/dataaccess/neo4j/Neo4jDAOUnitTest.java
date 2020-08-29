package rockets.dataaccess.neo4j;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import rockets.model.User;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db";
    private DAO dao;
    private Session session;
    private SessionFactory sessionFactory;

    private LaunchServiceProvider esa;
    private LaunchServiceProvider spacex;
    private Rocket rocket;

    @BeforeAll
    public void initializeNeo4j() {
        EmbeddedDriver driver = createEmbeddedDriver(TEST_DB);

        sessionFactory = new SessionFactory(driver, User.class.getPackage().getName());
        session = sessionFactory.openSession();
        dao = new Neo4jDAO(sessionFactory);
    }
    private static EmbeddedDriver createEmbeddedDriver(String fileDir) {
        File file = new File(fileDir);
        Configuration configuration = new Configuration.Builder()
                .uri(file.toURI().toString()) // For Embedded
                .build();
        EmbeddedDriver driver = new EmbeddedDriver();
        driver.configure(configuration);
        return driver;
    }

    @BeforeEach
    public void setup() {
        esa = new LaunchServiceProvider("ESA", 1970, "FR");
        spacex = new LaunchServiceProvider("SpaceX", 2002, "US");
        rocket = new Rocket("F9", "US", spacex);
    }

    @Test
    void shouldCreateNeo4jDAOSuccessfully() {
        assertNotNull(dao);
    }

    @Test
    void shouldCreateARocketSuccessfully() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");
        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull(graphRocket.getId());
        assertEquals(rocket, graphRocket);
        LaunchServiceProvider manufacturer = graphRocket.getManufacturer();
        assertNotNull(manufacturer.getId());
        assertEquals(rocket.getWikilink(), graphRocket.getWikilink());
        assertEquals(spacex, manufacturer);
    }

    @Test
    void shouldUpdateRocketAttributeSuccessfully() {
        rocket.setWikilink("https://en.wikipedia.org/wiki/Falcon_9");

        Rocket graphRocket = dao.createOrUpdate(rocket);
        assertNotNull(graphRocket.getId());
        assertEquals(rocket, graphRocket);

        String newLink = "http://adifferentlink.com";
        rocket.setWikilink(newLink);
        dao.createOrUpdate(rocket);
        graphRocket = dao.load(Rocket.class, rocket.getId());
        assertEquals(newLink, graphRocket.getWikilink());
    }

    @Test
    void shouldNotSaveTwoSameRockets() {
        assertNull(spacex.getId());

        Rocket rocket1 = new Rocket("F9", "US", spacex);
        Rocket rocket2 = new Rocket("F9", "US", spacex);
        assertEquals(rocket1, rocket2);
        dao.createOrUpdate(rocket1);
        assertNotNull(spacex.getId());
        Collection<Rocket> rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
        Collection<LaunchServiceProvider> manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, manufacturers.size());
        dao.createOrUpdate(rocket2);
        manufacturers = dao.loadAll(LaunchServiceProvider.class);
        assertEquals(1, manufacturers.size());
        rockets = dao.loadAll(Rocket.class);
        assertEquals(1, rockets.size());
    }

    @Test
    void shouldLoadAllRockets() {
        Set<Rocket> rockets = Sets.newHashSet(
                new Rocket("Ariane4", "FR", esa),
                new Rocket("F5", "US", spacex),
                new Rocket("BFR", "US", spacex)
        );

        for (Rocket r : rockets) {
            dao.createOrUpdate(r);
        }

        Collection<Rocket> loadedRockets = dao.loadAll(Rocket.class);
        assertEquals(rockets.size(), loadedRockets.size());
        for (Rocket r : rockets) {
            assertTrue(rockets.contains(r));
        }
    }

    @Test
    void shouldCreateALaunchSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2017, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);
        assertFalse(launches.isEmpty());
        assertTrue(launches.contains(launch));
    }


    @Test
    void shouldUpdateLaunchAttributesSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2017, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);

        Collection<Launch> launches = dao.loadAll(Launch.class);

        Launch loadedLaunch = launches.iterator().next();
        assertNull(loadedLaunch.getFunction());

        launch.setFunction("experimental");
        dao.createOrUpdate(launch);
        launches = dao.loadAll(Launch.class);
        assertEquals(1, launches.size());
        loadedLaunch = launches.iterator().next();
        assertEquals("experimental", loadedLaunch.getFunction());
    }

    @Test
    void shouldDeleteRocketWithoutDeleteLSP() {
        dao.createOrUpdate(rocket);
        assertNotNull(rocket.getId());
        assertNotNull(rocket.getManufacturer().getId());
        assertFalse(dao.loadAll(Rocket.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
        dao.delete(rocket);
        assertTrue(dao.loadAll(Rocket.class).isEmpty());
        assertFalse(dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    @Test
    void shouldDeleteRocketSuccessfully() {
        dao.createOrUpdate(rocket);
        assertNotNull(rocket.getId());
        dao.delete(rocket);
        assertTrue(dao.loadAll(Rocket.class).isEmpty());
    }

    @Test
    void shouldDeleteLaunchSuccessfully() {
        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(2020, 1, 1));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchSite("VAFB");
        launch.setOrbit("LEO");
        dao.createOrUpdate(launch);
        assertNotNull(launch.getId());
        dao.delete(launch);
        assertTrue(dao.loadAll(Launch.class).isEmpty());
    }

    @Test
    void shouldDeleteLaunchServiceProviderSuccessfully() {
        LaunchServiceProvider provider = esa;
        dao.createOrUpdate(provider);
        assertNotNull(provider.getId());
        dao.delete(provider);
        assertTrue(dao.loadAll(LaunchServiceProvider.class).isEmpty());
    }

    @AfterEach
    public void tearDown() {
        session.purgeDatabase();
    }

    @AfterAll
    public void closeNeo4jSession() {
        session.clear();
        sessionFactory.close();
    }
}