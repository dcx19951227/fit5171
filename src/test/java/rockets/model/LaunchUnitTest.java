package rockets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Description TODO
 * @Author Jianhai Wang
 * @ClassName LaunchUnitTest
 * @Date 2020/8/5 15:11
 * @Version 1.0
 */


class LaunchUnitTest {
    private Launch target;

    @BeforeEach
    public void setUp() {
        target = new Launch();
    }

    //Test launchDate is  null
    @DisplayName("Should throw exception when null is passed to setLaunchDate method")
    @Test

    void shouldThrowExceptionWhenSetLaunchDateToNull()
    {

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setLaunchDate(null));
        assertEquals("launchDate cannot be null", exception.getMessage());
    }

    //Test launch vehicle is  null
    @DisplayName("Should throw exception when null is passed to setLaunchVehicle method")
    @Test

    void shouldThrowExceptionWhenSetLaunchVehicleToNull()
    {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setLaunchVehicle(null));
        assertEquals("launchVehicle cannot be null", exception.getMessage());
    }

    //test launch service provide is null
    @DisplayName("Should throw exception when null is passed to setLaunchServiceProvider method")
    @Test

    void shouldThrowExceptionWhenSetLaunchServiceProviderToNull()
    {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setLaunchServiceProvider(null));
        assertEquals("launchServiceProvider cannot be null", exception.getMessage());
    }

    //test payload is null
    @DisplayName("Should throw exception when null is passed to setPayload method")
    @Test

    void shouldThrowExceptionWhenSetPayloadToNull()
    {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setPayload(null));
        assertEquals("payload cannot be null", exception.getMessage());
    }

    //test launch site is empty
    @DisplayName("Should throw exception when empty string or string of only spaces is passed to setLaunchSite method")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    void shouldThrowExceptionWhenSetLaunchSiteToEmptyString(String arg) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setLaunchSite(arg));
        assertEquals("launchSite cannot be null or empty", exception.getMessage());
    }

    //test launch site is null
    @DisplayName("Should throw exception when null is passed to setLaunchSite method")
    @Test
    void shouldThrowExceptionWhenSetLaunchSiteToNull()
    {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setLaunchSite(null));
        assertEquals("launchSite cannot be null or empty", exception.getMessage());
    }

    //test orbit is empty
    @DisplayName("Should throw exception when empty string or string of only spaces is passed to setOrbit method")
    @ParameterizedTest
    @ValueSource(strings = {"","  "})
    void shouldThrowExceptionWhenSetOrbitToEmptyString(String arg)
    {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setOrbit(arg));
        assertEquals("orbit cannot be null or empty", exception.getMessage());
    }

    //test orbit is null
    @DisplayName("Should throw exception when null is passed to setOrbit method")
    @Test
    void shouldThrowExceptionWhenSetOrbitToNull()
    {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setOrbit(null));
        assertEquals("orbit cannot be null or empty", exception.getMessage());
    }

    //test function is empty
    @DisplayName("Should throw exception when empty string or string of only spaces is passed to setFunction method")
    @ParameterizedTest

    @ValueSource(strings = {"","  "})
    void shouldThrowExceptionWhenSetFunctionToEmptyString(String arg)
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setFunction(arg));
        assertEquals("function cannot be null or empty", exception.getMessage());
    }

    //test function is null
    @DisplayName("Should throw exception when null is passed to setFunction method")
    @Test

    void shouldThrowExceptionWhenSetFunctionToNull()
    {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setFunction(null));
        assertEquals("function cannot be null or empty", exception.getMessage());
    }

    //test price is null
    @DisplayName("Should throw exception when null is passed to setPrice method")
    @Test
    void shouldThrowExceptionWhenSetPriceToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setPrice(null));
        assertEquals("price cannot be null", exception.getMessage());
    }

    //test price is zero
    @DisplayName("Should throw exception when zero is passed to setPrice method")
    @ParameterizedTest
    @ValueSource(ints = 0)
    void shouldThrowExceptionWhenSetPriceToNoPositive(int value) {
        System.out.println(value);
        BigDecimal price = new BigDecimal(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> target.setPrice(price));
        assertEquals("price cannot be zero or negative", exception.getMessage());
    }

    //test price is positive
    @DisplayName("Should set price when positive value is passed to setPrice method")
    @ParameterizedTest
    @ValueSource(ints = 2)
    void shouldSetPriceToPositiveValue(int value) {
        BigDecimal price = new BigDecimal(value);
        target.setPrice(price);
        assertEquals(target.getPrice(), price);
    }

    //test launch outcome is null
    @DisplayName("Should throw exception when null is passed to setLaunchOutcome method")
    @Test
    void shouldThrowExceptionWhenSetLaunchOutcomeToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> target.setLaunchOutcome(null));
        assertEquals("launchOutcome cannot be null", exception.getMessage());
    }

    //test target equal null is false
    @DisplayName("Should return false when null is passed to equals method")
    @Test
    void shouldReturnFalseWhenNullPassedToEquals() {
        assertNotEquals(null, target);
    }

    //test different class type return false
    @DisplayName("Should return false when string is passed to equals method")
    @Test
    void shouldReturnFalseWhenStringPassedToEquals() {
        assertNotEquals("text", target);
    }

    //test same content object should return ture
    @DisplayName("Equals method should return true when two launch objects are equal")
    @Test
    void equalsMethodShouldReturnTrueWhenTwoLaunchObjectsAreEqual() {
        Rocket rocket = new Rocket("W2M", "AU", new LaunchServiceProvider("name", 2000, "AU"));
        LaunchServiceProvider serviceProvider = new LaunchServiceProvider("Antrix", 1992, "AU");
        String orbit = "low earth orbit";
        target.setLaunchDate(LocalDate.of(1995, 9, 27));
        target.setLaunchVehicle(rocket);
        target.setLaunchServiceProvider(serviceProvider);
        target.setOrbit(orbit);
        //target.setOrbit("High earth orbit");

        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(1995, 9, 27));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchServiceProvider(serviceProvider);
        launch.setOrbit(orbit);
        assertEquals(true, target.equals(launch));
    }

    //test two objects is same except for orbit should return false
    @DisplayName("Equals method should return false when two launch objects are not equal")
    @Test
    void equalsMethodShouldReturnFalseWhenTwoLaunchObjectsAreNotEqual() {
        Rocket rocket = new Rocket("W2M", "UK", new LaunchServiceProvider("name", 2000, "AU"));
        LaunchServiceProvider serviceProvider = new LaunchServiceProvider("name", 2000, "AU");
        String orbit = "low earth orbit";
        target.setLaunchDate(LocalDate.of(1998, 9, 27));
        target.setLaunchVehicle(rocket);
        target.setLaunchServiceProvider(serviceProvider);
        target.setOrbit(orbit);

        Launch launch = new Launch();
        launch.setLaunchDate(LocalDate.of(1995, 9, 27));
        launch.setLaunchVehicle(rocket);
        launch.setLaunchServiceProvider(serviceProvider);
        launch.setOrbit("Geostationary orbit");
        assertNotEquals(target, launch);
    }
}
