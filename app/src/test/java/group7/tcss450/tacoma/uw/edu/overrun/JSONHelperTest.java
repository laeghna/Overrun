package group7.tcss450.tacoma.uw.edu.overrun;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import group7.tcss450.tacoma.uw.edu.overrun.Utils.JSONHelper;

import static junit.framework.Assert.fail;

/**
 * Test class for the JSONHelper class.
 * @author Leslie Pedro
 */
public class JSONHelperTest {

    /** A JSONObject for use in testing. */
    JSONObject testJSON;

    /** Sets up the JSONObject. */
    @Before
    public void setup() {
        testJSON = new JSONObject();
    }

    /** Tests the tryGetString method with a null JSONObject. */
    @Test
    public void tryGetStringNullJSONObjectTest() {
        try {
            JSONHelper.tryGetString(null, "value");
            fail("JSONHelper tryGetString accepted null JSONObject.");
        } catch (IllegalArgumentException e) {
            //test passed
        }
    }

    /** Tests the tryGetString method with a null value. */
    @Test
    public void tryGetStringNullValueTest() {
        try {
            JSONHelper.tryGetString(testJSON, null);
            fail("JSONHelper tryGetString accepted null value.");
        } catch (IllegalArgumentException e) {
            //test passed
        }
    }

    /** Tests the tryGetBoolean with a null JSONObject. */
    @Test
    public void tryGetBooleanNullJSONObjectTest() {
        try {
            JSONHelper.tryGetBoolean(null, "Value");
            fail("JSONHelper tryGetBoolean accepted null JSONObject.");
        } catch(IllegalArgumentException e ) {
            //test passed
        }
    }

    /** Tests the tryGetBoolean with a null value. */
    @Test
    public void tryGetBooleanNullValueTest() {
        try {
            JSONHelper.tryGetBoolean(testJSON, null);
            fail("JSONHelper tryGetBoolean accepted null value.");
        } catch(IllegalArgumentException e) {
            //test passed
        }
    }
}
