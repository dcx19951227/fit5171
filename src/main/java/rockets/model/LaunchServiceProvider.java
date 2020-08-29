package rockets.model;

import com.google.common.collect.Sets;

import java.util.*;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class LaunchServiceProvider extends Entity {
    private String name;

    private int yearFounded;

    private String country;

    private String headquarters;

    private Set<Rocket> rockets;

    public LaunchServiceProvider(String name, int yearFounded, String country) {
        notNull(name);
        notNull(yearFounded);
        notNull(country);

        if (isValidName(name) && isValidYearFounded(yearFounded) && isValidCountry(country)) {
            this.name = name;
            this.yearFounded = yearFounded;
            this.country = country;
            rockets = Sets.newLinkedHashSet();
        }
        else
            throw new IllegalArgumentException("Value should be valid");
    }

    public String getName() {
        return name;
    }

    public int getYearFounded() {
        return yearFounded;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadquarters() {
        return headquarters;
    }

    public Set<Rocket> getRockets() {
        return rockets;
    }

    public void setHeadquarters(String headquarters) {
        notBlank(headquarters, "headquarters cannot be null or empty");
        if (isValidHeadquarters(headquarters))
            this.headquarters = headquarters;
        else
            throw new IllegalArgumentException("Headquarters should only contain letters.");
    }

    public void setRockets(Set<Rocket> rockets) {
        this.rockets = rockets;
    }

    public void setName(String name) {
        notBlank(name, "The launch service provider name cannot be null or empty");
        if (!isValidName(name))
            throw new IllegalArgumentException("The launch service provider name should only contain letters and number");
        else
            this.name = name;
    }

    public void setYearFounded(int year) {
        notNull(year,"year cannot be null or empty");
        if (isValidYearFounded(year))
            this.yearFounded = year;
        else
            throw new IllegalArgumentException("year should be between 1900 and 2020.");
    }

    public void setCountry(String country) {
        notBlank(country, "country cannot be null or empty");
        if (isValidCountry(country))
            this.country = country;
        else
            throw new IllegalArgumentException("Please enter valid country.");
    }

    public boolean isValidName(String name)
    {
        return name.matches("[0-9a-zA-Z]*");
    }

    public boolean isValidYearFounded(int yearFounded)
    {
        return yearFounded >= 1900 && yearFounded <= 2020;
    }

    public boolean isValidCountry(String country)
    {
        Set<String> isoCountries = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
        return isoCountries.contains(country
        );
    }

    public boolean isValidHeadquarters(String headquarters)
    {
        return headquarters.matches("[a-zA-Z]*");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaunchServiceProvider that = (LaunchServiceProvider) o;
        return yearFounded == that.yearFounded &&
                Objects.equals(name, that.name) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, yearFounded, country);
    }
}
