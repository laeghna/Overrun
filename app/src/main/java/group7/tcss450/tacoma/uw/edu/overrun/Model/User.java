package group7.tcss450.tacoma.uw.edu.overrun.Model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * User model for API client to serialize json objects into a User object.
 *
 * @author Ethan Rowell
 * @version 2 NDec 2016
 */
public class User {

    /**
     * The user's email.
     */
    @SerializedName("email")
    private String email;

    /**
     * The user's salt
     */
    @SerializedName("salt")
    private String salt;

    /**
     * The user's hash.
     */
    @SerializedName("hash")
    private String hash;

    /**
     * Email was verified
     */
    @SerializedName("email_verified")
    private String emailVerified;

    /**
     * * User's first name pulled from Google Sign in.
     */
    @SerializedName("firstName")
    private String firstName;

    /**
     * User's last name pulled from Google Sign in.
     */
    @SerializedName("lastName")
    private String lastName;

    /**
     * Public constructer for the User object.
     *
     * @param email the email for the User.
     * @param hash  the hash for the User.
     * @param salt  the salt for the User.
     * @throws IllegalArgumentException if any of the String parameters are null.
     */
    public User(String email, String salt, String hash) {
        if (email == null || salt == null || hash == null) {
            throw new IllegalArgumentException("Null values not allowed for User.");
        }
        this.email = email;
        this.salt = salt;
        this.hash = hash;
    }

    /**
     * Gets the hash for this user object.
     *
     * @return the hash for this user.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the hash for the user.
     *
     * @param hash the new hash for the user.
     * @throws IllegalArgumentException if the hash is null.
     */
    public void setHash(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("User hash cannot be null.");
        }
        this.hash = hash;
    }

    /**
     * Gets the salt for this User object.
     *
     * @return the salt for this User.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Sets the salt for the user.
     *
     * @param salt the new salt for the user.
     * @throws IllegalArgumentException if the salt is null.
     */
    public void setSalt(String salt) {
        if (salt == null) {
            throw new IllegalArgumentException("User salt cannot be null.");
        }
        this.salt = salt;
    }

    /**
     * Gets the email for this user object.
     *
     * @return the email for this user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email for the User.
     *
     * @param email the new email for the user.
     * @throws IllegalArgumentException if the email is null
     */
    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("User email cannot be null.");
        }
        this.email = email;
    }

    /**
     * Gets the user's first name. May be null.
     * @return user's first name.
     */
    @Nullable
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     * @param firstName User's first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name. May be null.
     * @return
     */
    @Nullable
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     * @param lastName user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
