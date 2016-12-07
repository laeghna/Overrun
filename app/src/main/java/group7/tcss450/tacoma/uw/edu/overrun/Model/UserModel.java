package group7.tcss450.tacoma.uw.edu.overrun.Model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * UserModel model for API client to serialize json objects into a UserModel object.
 *
 * @author Ethan Rowell
 * @version 2 NDec 2016
 */
public class UserModel {

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
     * Public constructor for the UserModel object.
     *
     * @param email the email for the User.
     * @param hash  the hash for the User.
     * @param salt  the salt for the User.
     * @throws IllegalArgumentException if any of the String parameters are null.
     */
    public UserModel(String email, String salt, String hash) {
        if (email == null || salt == null || hash == null) {
            throw new IllegalArgumentException("Null values not allowed for UserModel.");
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
            throw new IllegalArgumentException("UserModel hash cannot be null.");
        }
        this.hash = hash;
    }

    /**
     * Gets the salt for this UserModel object.
     *
     * @return the salt for this UserModel.
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
            throw new IllegalArgumentException("UserModel salt cannot be null.");
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
     * Sets the email for the UserModel.
     *
     * @param email the new email for the user.
     * @throws IllegalArgumentException if the email is null
     */
    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("UserModel email cannot be null.");
        }
        this.email = email;
    }

    /**
     * Gets the user's first name. May be null.
     *
     * @return user's first name.
     */
    @Nullable
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName User's first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name. May be null.
     *
     * @return gets the user's last name
     */
    @Nullable
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName user's last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
