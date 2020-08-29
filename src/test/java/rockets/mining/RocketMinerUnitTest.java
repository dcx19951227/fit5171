package rockets.mining;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RocketMinerUnitTest {
    Logger logger = LoggerFactory.getLogger(RocketMinerUnitTest.class);

    private DAO dao;
    private RocketMiner miner;
    private List<Rocket> rockets;
    private List<LaunchServiceProvider> lsps;
    private List<Launch> launches;

    @BeforeEach
    public void setUp() {
        dao = mock(Neo4jDAO.class);
        miner = new RocketMiner(dao);
        rockets = Lists.newArrayList();

        lsps = Arrays.asList(
                new LaunchServiceProvider("ULA", 1990, "US"),
                new LaunchServiceProvider("SpaceX", 2002, "US"),
                new LaunchServiceProvider("ESA", 1975, "AU")
        );

        // index of lsp of each rocket
        int[] lspIndex = new int[]{0, 0, 0, 1, 1};
        // 5 rockets
        for (int i = 0; i < 5; i++) {
            rockets.add(new Rocket("rocket_" + i, "US", lsps.get(lspIndex[i])));
        }
        // month of each launch
        int[] months = new int[]{1, 6, 4, 3, 4, 11, 6, 5, 12, 5};

        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3};

        Launch.LaunchOutcome outcomes[] = Launch.LaunchOutcome.values();
        //index of launchOutcome of each launch
        int[] outcomeIndex = new int[]{0, 0, 0, 1, 1, 1, 1, 1, 0, 1};

        // index of price of each launch
        BigDecimal[] priceIndex = {new BigDecimal(1000), new BigDecimal(1000), new BigDecimal(1000), new BigDecimal(1000)
                , new BigDecimal(2000), new BigDecimal(2000), new BigDecimal(2000), new BigDecimal(3000),
                new BigDecimal(3000), new BigDecimal(4000)};

        BigDecimal price = new BigDecimal("25000.99");

        // 10 launches
        launches = IntStream.range(0, 10).mapToObj(i -> {
            logger.info("create " + i + " launch in month: " + months[i]);
            Launch l = new Launch();
            l.setLaunchDate(LocalDate.of(2017, months[i], 1));
            l.setLaunchVehicle(rockets.get(rocketIndex[i]));
            l.setLaunchServiceProvider(rockets.get(rocketIndex[i]).getManufacturer());
            l.setLaunchSite("VAFB");
            l.setPrice(priceIndex[i]);
            l.setOrbit("LEO");
            l.setLaunchOutcome(outcomes[outcomeIndex[i]]);
            spy(l);
            return l;
        }).collect(Collectors.toList());
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldReturnTopMostRecentLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        sortedLaunches.sort((a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate()));
        List<Launch> loadedLaunches = miner.mostRecentLaunches(k);
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
    }


    @ParameterizedTest
    @ValueSource(strings = {"LEO"})
    void shouldReturnCountryWithMaximumRockets(String orbit) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        String country = miner.dominantCountry(orbit);
        RocketMiner mockRM=mock(RocketMiner.class);
        when(mockRM.dominantCountry(orbit)).thenReturn("US");
        assertEquals(mockRM.dominantCountry(orbit),country);
    }

    @ParameterizedTest
    @ValueSource(strings = {"LEO"})
    void negativeTestForDominantCountry(String orbit) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);

        String country = miner.dominantCountry(orbit);
        RocketMiner mockRM=mock(RocketMiner.class);
        when(mockRM.dominantCountry(orbit)).thenReturn("europe");
        assertNotEquals(mockRM.dominantCountry(orbit),country);
    }

    @ParameterizedTest
    @ValueSource(strings = {""," "})
    void shouldThrowExceptionWhenInputToDominantCountryIsEmpty(String orbit) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class,()->miner.dominantCountry(orbit));
        assertEquals("orbit should not be empty",exception.getMessage());


    }

    @Test
    void shouldThrowExceptionWhenInputToDominantCountryIsNull() {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        //NullPointerException exception =  assertThrows(NullPointerException.class,()->miner.dominantCountry(null));
        NullPointerException exception =  assertThrows(NullPointerException.class,()->miner.dominantCountry(null));
        assertEquals("orbit should not be null",exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldReturnTopMostLaunchedRockets(int k) {
        when(dao.loadAll(Rocket.class)).thenReturn(rockets);
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Rocket> r = new ArrayList<>();
        r.add(new Rocket("rocket_" + 0, "US", new LaunchServiceProvider("ULA", 1990, "US")));
        r.add(new Rocket("rocket_" + 1, "US", new LaunchServiceProvider("ULA", 1990, "US")));
        r.add(new Rocket("rocket_" + 2, "US", new LaunchServiceProvider("ULA", 1990, "US")));
        r.add(new Rocket("rocket_" + 3, "US", new LaunchServiceProvider("SpaceX", 2002, "US")));
        //List<Rocket> mockRocket = spy(r);
        List mockRocket = mock(ArrayList.class);
        when(mockRocket.get(0)).thenReturn(r.get(0));
        List<Rocket> loadedRockets = miner.mostLaunchedRockets(k);
        assertEquals(k, loadedRockets.size());
        assertEquals(mockRocket.get(0), loadedRockets.get(0)); // checks the top rocket launched
        assertEquals(r.subList(0, k), loadedRockets); // checks all the top k rockets launched
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldReturnTopMostExpensiveLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        for (Launch l : sortedLaunches) {
            System.out.println(l.getPrice());
        }
        sortedLaunches.sort((a, b) -> -a.getPrice().compareTo(b.getPrice()));
        List<Launch> loadedLaunches = miner.mostExpensiveLaunches(k);
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
    }

    @ParameterizedTest
    @ValueSource(ints = {12})
    void shouldThrowExceptionWhenKIsLargerThanTheLaunchesInMostExpensiveLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> miner.mostExpensiveLaunches(k));
        assertEquals("k should less than the total number of launches", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = 1)
    void shouldReturnTheMostReliableLaunchServiceProviders(int k){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<LaunchServiceProvider> providers = new ArrayList<>();
        providers.add(new LaunchServiceProvider("SpaceX", 2002, "US"));
        List<LaunchServiceProvider> loadedProvider = miner.mostReliableLaunchServiceProviders(k);
        assertEquals(providers,loadedProvider);
    }

    @ParameterizedTest
    @ValueSource(ints = {12})
    void shouldThrowExceptionWhenKIsLargerThanTheReliableLaunchServiceProviders(int k)
    {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> miner.mostReliableLaunchServiceProviders(k));
        assertEquals("k should less than the total number of launches", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"2,2100"})
    void shouldThrowExceptionWhenYearIsNotInRangeInHighestRevenueLaunchServiceProviders(int k,int year)
    {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> miner.highestRevenueLaunchServiceProviders(k,year));
        assertEquals("There is no launch in this year", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"2,2017"})
    void shouldReturnTheHighestRevenueLaunchServiceProviders(int k, int year){

        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<LaunchServiceProvider> providers = new ArrayList<>();
        providers.add(new LaunchServiceProvider("ULA", 1990, "US"));
        providers.add(new LaunchServiceProvider("SpaceX", 2002, "US"));

        List<LaunchServiceProvider> loadedProvider = miner.highestRevenueLaunchServiceProviders(k,year);
        assertEquals(providers,loadedProvider);
    }

    @ParameterizedTest
    @CsvSource({"12,2017"})
    void shouldThrowExceptionWhenKIsLargerThanTheLaunchesInHighestRevenueLaunchServiceProviders(int k,int year)
    {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> miner.highestRevenueLaunchServiceProviders(k,year));
        assertEquals("k should less than the total number of launches", exception.getMessage());
    }

}