package group7.tcss450.tacoma.uw.edu.overrun.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Checks for the value given and returns the respective type.
 */
public class JSONHelper {

    /**
     * Checks for the string matching "val"
     * @param obj the JSON Object to check
     * @param val the property to check for
     * @throws IllegalArgumentException if obj or val are null.
     * @return The String or null if none found.
     */
    public static String tryGetString(JSONObject obj, String val) {
        if(obj == null || val == null) {
            throw new IllegalArgumentException("JSONObject/String cannot be null.");
        }
        try {
            if (obj.has(val)) {
                return (String) obj.get(val);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks for the property matching "val" in the JSONObject
     * @param obj the JSON Object to check
     * @param val the property to check for
     * @throws IllegalArgumentException if obj or val are null
     * @return The Boolean or null if none found.
     */
    public static Boolean tryGetBoolean(JSONObject obj, String val) {
        if(obj == null || val == null) {
            throw new IllegalArgumentException("JSONObject/String cannot be null.");
        }
        try {
            if (obj.has(val)) {
                return (Boolean) obj.get(val);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
