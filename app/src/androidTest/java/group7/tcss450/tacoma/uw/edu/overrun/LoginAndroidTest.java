package group7.tcss450.tacoma.uw.edu.overrun;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

/**
 * Tests the login fragment.
 *
 * @author Ethan Rowell
 * @version Dec 6, 2016
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginAndroidTest {

    /**
     * Test email.
     * This must be a registered user in order for these test to pass.
     */
    private String email = "blah@blah.com";

    /**
     * Test password.
     */
    private String password = "blahblah1@";


    /**
     * Rule for creating an activity.
     */
    @Rule
    public ActivityTestRule<SignInActivity> mActivityRule = new ActivityTestRule<>(
            SignInActivity.class);

    /**
     * Set up method before each test.
     *
     * @throws InterruptedException Exception for the Thread sleep.
     */
    @Before
    public void setUp() throws InterruptedException {
        Intents.init();
        TestHelperMethods.clearSharedPreferences(getTargetContext());
        mActivityRule.launchActivity(new Intent(getTargetContext(), StartMenuActivity.class));

        // wait for toasts to disappear
        Thread.sleep(4000);
    }

    /**
     * Handles tear down after tests.
     */
    @After
    public void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        Intents.release();
    }

    /**
     * Tests the activity change after sign in.
     */
    @Ignore
    //@Test
    public void testLogin_RouteToStartMenu() {
        onView(withId(R.id.email_login)).perform(typeText(email));
        onView(withId(R.id.password_login)).perform(typeText(password));

        onView(withId(R.id.login_button)).perform(click());

        assertCurrentActivityIsInstanceOf(StartMenuActivity.class);
    }

    /**
     * Tests a failed login attempt.
     */
    @Test
    public void testLogin_FailedLogin() {
        onView(withId(R.id.email_login)).perform(typeText(email));
        onView(withId(R.id.password_login)).perform(typeText(password + "1"));

        onView(withId(R.id.login_button)).perform(click());

        onView(withText("Email or password was incorrect."))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }


    /**
     * Tests a successful login.
     */
    @Test
    public void testLogin_ValidLogin() {
        onView(withId(R.id.email_login)).perform(typeText(email));
        onView(withId(R.id.password_login)).perform(typeText(password));

        onView(withId(R.id.login_button)).perform(click());

        onView(withText("Signed in as: " + email))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Ignore
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //@Test
    public void testLogin_NoNetworkConnection() throws InterruptedException {
        setMobileDataEnabled(getContext(), false);
        setWifiEnabled(getContext(), false);

        Thread.sleep(3000);

        onView(withId(R.id.email_login)).perform(typeText(email));
        onView(withId(R.id.password_login)).perform(typeText(password));

        onView(withId(R.id.login_button)).perform(click());

        onView(withText("A network connection is required to login."))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        setMobileDataEnabled(getContext(), true);
        setWifiEnabled(getContext(), true);
    }

    /**
     * Asserts instance of activity.
     *
     * @param activityClass The activity class to assert.
     */
    public void assertCurrentActivityIsInstanceOf(Class<? extends Activity> activityClass) {
        Context currentActivity = getContext();// getActivityInstance();
        checkNotNull(currentActivity);
        checkNotNull(activityClass);
        assertTrue(currentActivity.getClass().isAssignableFrom(activityClass));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setMobileDataEnabled(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the WiFi to enabled.
     * Not currently working...
     *
     * @param context Current context
     * @param enabled enabled or not
     */
    private void setWifiEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }
}
