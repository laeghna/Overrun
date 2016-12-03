package group7.tcss450.tacoma.uw.edu.overrun.Model;

import com.google.gson.annotations.SerializedName;

/**
 * User model for API client to serialize json objects into a User object.
 *
 * @author Ethan Rowell
 * @version 2 NDec 2016
 */
public class User {

    @SerializedName("email")
    private String email;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("email_verified")
    private String emailVerified;

    public User(String email, String firstName, String lastName, String  emailVerified) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailVerified = emailVerified;
    }


    public String getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
