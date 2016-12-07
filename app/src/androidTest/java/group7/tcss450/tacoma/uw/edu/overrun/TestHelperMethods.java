package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.content.SharedPreferences;


/**
 * Helper methods for testing.
 *
 * @author Ethan Rowell
 * @version Dec 6, 2016
 */
class TestHelperMethods {

    /**
     * Clears shared preferences.
     *
     * @param context current context
     */
    static void clearSharedPreferences(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(context.getResources().getString(R.string.shared_prefs),
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
