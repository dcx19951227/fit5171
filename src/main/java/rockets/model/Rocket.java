package rockets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.ogm.annotation.Relationship.INCOMING;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

public class Rocket extends Entity {
    @Property(name="name")
    private String name;

    @Property(name="country")
    private String country;

    @Relationship(type = "MANUFACTURES", direction = INCOMING)
    private LaunchServiceProvider manufacturer;

    @Property(name="massToLEO")
    private String massToLEO;

    @Property(name="massToGTO")
    private String massToGTO;

    @Property(name="massToOther")
    private String massToOther;

    @Property(name="firstYearFlight")
    private int firstYearFlight;

    public int getFirstYearFlight() {
        return firstYearFlight;
    }

    public void setFirstYearFlight(int firstYearFlight) {
        this.firstYearFlight = firstYearFlight;
    }

    public int getLatestYearFlight() {
        return latestYearFlight;
    }

    public void setLatestYearFlight(int latestYearFlight) {
        this.latestYearFlight = latestYearFlight;
    }

    @Property(name="lastYearFlight")
    private int latestYearFlight;

    @Relationship(type = "PROVIDES", direction = OUTGOING)
    @JsonIgnore
    private Set<Launch> launches;

    public Rocket() {
        super();
    }

    public Rocket(String name, String country, LaunchServiceProvider manufacturer) {
        notNull(name);
        notNull(country);
        notNull(manufacturer);

        this.name = name;
        this.country = country;
        this.manufacturer = manufacturer;

        this.launches = new LinkedHashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        notBlank(name, "name cannot be null or empty");
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        notBlank(country, "country cannot be null or empty");
        if (isCountryValid(country))
            this.country = country;
        else
            throw new IllegalArgumentException("Country should be a valid country in country code (AU for Australia)");
    }

    public LaunchServiceProvider getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(LaunchServiceProvider manufacturer) {
        if (manufacturer == null) {
            throw new NullPointerException("Manufacturer cannot be null");
        }
        this.manufacturer = manufacturer;
    }

    public String getMassToLEO() {
        return massToLEO;
    }

    public String getMassToGTO() {
        return massToGTO;
    }

    public String getMassToOther() {
        return massToOther;
    }

    public void setMassToLEO(String massToLEO) {
        this.massToLEO = massToLEO;
    }

    public void setMassToGTO(String massToGTO) {
        this.massToGTO = massToGTO;
    }

    public void setMassToOther(String massToOther) {
        this.massToOther = massToOther;
    }

    public boolean isCountryValid(String country) {

        Set<String> isoCountries = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
        return isoCountries.contains(country);
    }
    public Set<Launch> getLaunches() {
        return launches;
    }

    public void setLaunches(Set<Launch> launches) {
        this.launches = launches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(name, rocket.name) &&
                Objects.equals(country, rocket.country) &&
                Objects.equals(manufacturer, rocket.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, manufacturer);
    }

    @Override
    public String toString() {
        return "Rocket{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", massToLEO='" + massToLEO + '\'' +
                ", massToGTO='" + massToGTO + '\'' +
                ", massToOther='" + massToOther + '\'' +
                '}';
    }
}
