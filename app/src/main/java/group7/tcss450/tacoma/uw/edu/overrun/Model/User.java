package group7.tcss450.tacoma.uw.edu.overrun.Model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("email")
    private String email;

    @SerializedName("salt")
    private String salt;

    @SerializedName("hash")
    private String hash;

    public User(String email, String salt, String hash) {
        this.email = email;
        this.salt = salt;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
