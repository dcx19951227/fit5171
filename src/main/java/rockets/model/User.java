package rockets.model;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

public class User extends Entity {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        notNull(firstName, "First name cannot be null or empty");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        notNull(lastName, "Last name cannot be null or empty");
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        notBlank(email, "email cannot be null or empty");
        notNull(email, "email cannot be null or empty");
        String emailFormat = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        matchesPattern(email, emailFormat, "email format is invalid");
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        notBlank(password, "password cannot be null or empty");
        String passwordFormat = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        matchesPattern(password, passwordFormat, "password must contain 8 - 16 characters and composed by number and characters");
        this.password = password;
    }

    // match the given password against user's password and return the result
    public boolean isPasswordMatch(String password) {
        return this.password.equals(password.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
