package group7.tcss450.tacoma.uw.edu.overrun;

import org.junit.Before;
import org.junit.Test;

import group7.tcss450.tacoma.uw.edu.overrun.Model.UserModel;

import static junit.framework.Assert.fail;

/**
 * A Test Class for the Model.UserModel class.
 * @author Leslie Pedro
 */
public class UserModelTest {

    UserModel testUser;

    /**
     * Sets up a test user with valid parameters for use while
     * testing setter methods.
     */
    @Before
    public void setup() {
        testUser = new UserModel("email", "salt", "hash");
    }

    /** Testing the UserModel constructor with a null email address. */
    @Test
    public void userConstructorBadEmailTest() {
        try {
            new UserModel(null, "salt", "hash");
            fail("Null email given, UserModel still created.");
        } catch (IllegalArgumentException e) {
            // Test passed.
        }
    }

    /** Testing the UserModel constructor with a null hash value. */
    @Test
    public void userConstructorBadHashTest() {
        try {
            new UserModel("email", "salt", null);
            fail("Null hash given, UserModel still created.");
        } catch (IllegalArgumentException e) {
            // Test passed.
        }
    }

    /** Testing the UserModel constructor with a null salt value. */
    @Test
    public void userConstructorBadSaltTest() {
        try {
            new UserModel("email", null, "hash");
            fail("Null email given, UserModel still created.");
        } catch (IllegalArgumentException e) {
            // Test passed.
        }
    }

    /** Tests the UserModel function setEmail with a null email. */
    @Test
    public void userEmailSetterNullEmailTest() {
        try {
            testUser.setEmail(null);
            fail("Failure: email set to null");
        } catch(IllegalArgumentException e) {
            // test passed
        }
    }

    /** Tests the user function setSalt with a null salt value. */
    @Test
    public void userSaltSetterNullSaltTest() {
        try {
            testUser.setSalt(null);
            fail("Failure: salt set to null.");
        } catch (IllegalArgumentException e) {
            //test passed
        }
    }

    /** Tests the user function setHash with a null hash value. */
    @Test
    public void userHashSetterNullHashTest() {
        try {
            testUser.setHash(null);
            fail("Failure: hash set to null.");
        } catch (IllegalArgumentException e) {
            //test passed
        }
    }
}
