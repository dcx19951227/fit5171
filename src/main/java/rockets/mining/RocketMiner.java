package rockets.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.*;

public class RocketMiner {
    private static Logger logger = LoggerFactory.getLogger(RocketMiner.class);
    private static final  String MESSAGE = "k should less than the total number of launches";

    private DAO dao;

    public RocketMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the top-k most active rockets, as measured by number of completed launches.
     *
     * @param k the number of rockets to be returned.
     * @return the list of k most active rockets.
     */
    public List<Rocket> mostLaunchedRockets(int k) {
        logger.info("find most active " + k + " rockets");
        Collection<Launch> launchesList = dao.loadAll(Launch.class);
        List<Rocket> r = new ArrayList<>();

        for (Launch l : launchesList) {
            if (l.getLaunchDate() != null) {
                r.add(l.getLaunchVehicle());
            }
        }
        Map<Rocket, Long> hashmap = r.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<Rocket, Long> sorted = hashmap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        List<Rocket> rockets = new ArrayList<>();
        for (Map.Entry<Rocket, Long> e : sorted.entrySet()) {
            rockets.add(e.getKey());
        }
        return rockets.stream().limit(k).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns the top-k most reliable launch service providers as measured
     * by percentage of successful launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most reliable ones.
     */
    public List<LaunchServiceProvider> mostReliableLaunchServiceProviders(int k) {
        logger.info("find most reliable " + k + " launch service providers");
        Collection<Launch> launches = dao.loadAll(Launch.class);
        if (k > launches.size())
            throw new IllegalArgumentException(MESSAGE);
        // find all the providers
        List<LaunchServiceProvider> allProviders = new ArrayList<>();
        for (Launch launch: launches)
        {
            LaunchServiceProvider provider = launch.getLaunchServiceProvider();
            allProviders.add(provider);
        }
        List<LaunchServiceProvider> distinctProviders = allProviders.stream().distinct().collect(Collectors.toList());
        //create hashMap of provider and it's successful rate then add value in it
        HashMap<LaunchServiceProvider,Double> map = new HashMap<>();
        for (LaunchServiceProvider provider: distinctProviders)
        {
            int success = 0;
            int fail = 0;
            for (Launch launch: launches)
            {
                if (provider.getName().equals(launch.getLaunchServiceProvider().getName()))
                {
                    switch(launch.getLaunchOutcome())
                    {
                        case FAILED:
                            fail++;
                        case SUCCESSFUL:
                            success++;
                    }
                }
            }

            if(success+fail!=0) {
                double successfulRate = (double)success / (success + fail);
                map.put(provider,successfulRate);
            }

        }
        // Sort HashMap with K elements by successful rate
        Comparator<Map.Entry<LaunchServiceProvider,Double>> successfulRateComparator = (a, b) -> -a.getValue().compareTo(b.getValue());
        HashMap<LaunchServiceProvider,Double> sorted = map.entrySet().stream().sorted(successfulRateComparator).limit(k)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        // Create a list for sorted providers
        List<LaunchServiceProvider> sortedlaunchProviderList = new ArrayList<>();
        for (Map.Entry<LaunchServiceProvider,Double> en : sorted.entrySet())
        {
            sortedlaunchProviderList.add(en.getKey());
        }
        return sortedlaunchProviderList;


    }

    /**
     * <p>
     * Returns the top-k most recent launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most recent launches.
     */
    public List<Launch> mostRecentLaunches(int k) {
        logger.info("find most recent " + k + " launches");
        Collection<Launch> launches = dao.loadAll(Launch.class);
        Comparator<Launch> launchDateComparator = (a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate());
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns the dominant country who has the most launched rockets in an orbit.
     *
     * @param orbit the orbit
     * @return the country who sends the most payload to the orbit
     */
    public String dominantCountry(String orbit) {
        notNull(orbit,"orbit should not be null");
        if(orbit.equals("") || orbit.equals(" "))
            throw new IllegalArgumentException("orbit should not be empty");

        logger.info("find the dominant country who has the most launched rockets in the orbit " + orbit );
        Collection<Launch> launchesList=dao.loadAll(Launch.class);
        HashMap<String,Integer> dictionary=new HashMap<>();
        Iterator<Launch> launchIterator = launchesList.iterator();
        while(launchIterator.hasNext())
        {
            Launch launch = launchIterator.next();
            String launchOrbit = launch.getOrbit().trim();
            String country = launch.getLaunchVehicle().getCountry().trim();
            if(launchOrbit.equalsIgnoreCase(orbit.trim()))
            {
                if(dictionary.containsKey(country))
                {
                    int numberOfRockets = dictionary.get(country);
                    numberOfRockets += 1;
                    dictionary.replace(country,numberOfRockets);
                }
                else
                    dictionary.put(country,1);
            }

        }

        //Finding maximum rockets sent by a country
        String maxCountry="";
        int maxRockets=0;
        Iterator<String> dicIterator = dictionary.keySet().iterator();
        while(dicIterator.hasNext())
        {
            String country = dicIterator.next();
            int rockets = dictionary.get(country);
            if( rockets > maxRockets)
            {
                maxCountry = country;
                maxRockets = rockets;
            }
        }

        return maxCountry;
    }

    /**
     * <p>
     * Returns the top-k most expensive launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most expensive launches.
     */
    public List<Launch> mostExpensiveLaunches(int k) {
        logger.info("find most expensive " + k + " launches");
        Collection<Launch> launches = dao.loadAll(Launch.class);
        if (k > launches.size())
            throw new IllegalArgumentException(MESSAGE);
        Comparator<Launch> launchDateComparator = (a, b) -> -a.getPrice().compareTo(b.getPrice());
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns a list of launch service provider that has the top-k highest
     * sales revenue in a year.
     *
     * @param k    the number of launch service provider.
     * @param year the year in request
     * @return the list of k launch service providers who has the highest sales revenue.
     */
    public List<LaunchServiceProvider> highestRevenueLaunchServiceProviders(int k, int year) {
        logger.info("find most highest sales revenue " + k + " launch service providers in " + year);
        Collection<Launch> launches = dao.loadAll(Launch.class);
        if (k > launches.size())
            throw new IllegalArgumentException(MESSAGE);
        // find all the providers in a specific year
        List<LaunchServiceProvider> allProviders = new ArrayList<>();
        for (Launch launch: launches)
        {
            if (launch.getLaunchDate().getYear() == year)
            {
                LaunchServiceProvider provider = launch.getLaunchServiceProvider();
                allProviders.add(provider);
            }
        }
        if (allProviders.isEmpty())
            throw new IllegalArgumentException("There is no launch in this year");
        List<LaunchServiceProvider> distinctProviders = allProviders.stream().distinct().collect(Collectors.toList());
        //create hashMap of provider and it's revenue and add value in it
        HashMap<LaunchServiceProvider, BigDecimal> map = new HashMap<>();
        for (LaunchServiceProvider provider: distinctProviders)
        {
            BigDecimal price = new BigDecimal(0);
            for (Launch launch: launches)
            {
                if (provider.getName().equals(launch.getLaunchServiceProvider().getName()))
                {
                    price.add(launch.getPrice());
                }
            }
            map.put(provider,price);
        }
        // Sort HashMap with K elements by the revenue
        Comparator<Map.Entry<LaunchServiceProvider,BigDecimal>> priceComparator = (a, b) -> -a.getValue().compareTo(b.getValue());
        HashMap<LaunchServiceProvider,BigDecimal> sorted = map.entrySet().stream().sorted(priceComparator).limit(k)
                .collect(Collectors.toMap(e -> e.getKey(),e -> e.getValue(), (e1,e2) -> e2, LinkedHashMap::new));
        // Create a list for sorted providers
        List<LaunchServiceProvider> sortedlaunchProviderList = new ArrayList<>();
        for (Map.Entry<LaunchServiceProvider,BigDecimal> en : sorted.entrySet())
        {
            sortedlaunchProviderList.add(en.getKey());
        }
        return sortedlaunchProviderList;
    }
}
