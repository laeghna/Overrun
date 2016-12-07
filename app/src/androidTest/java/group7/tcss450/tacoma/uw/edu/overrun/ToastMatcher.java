package group7.tcss450.tacoma.uw.edu.overrun;


import android.os.IBinder;
import android.support.test.espresso.Root;
import android.view.WindowManager;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Checks that the toast exists.
 *
 * @author Ethan Rowell
 * @version Dec 6, 2016
 */
class ToastMatcher extends TypeSafeMatcher<Root> {

    /**
     * Describes the toast.
     *
     * @param description the toast description
     */
    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    /**
     * Makes a safe match.
     *
     * @param root view root
     * @return returns whether it matches or not.
     */
    @Override
    public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            if (windowToken == appToken) {
                //means this window isn't contained by any other windows.
                return true;
            }
        }
        return false;
    }
}
