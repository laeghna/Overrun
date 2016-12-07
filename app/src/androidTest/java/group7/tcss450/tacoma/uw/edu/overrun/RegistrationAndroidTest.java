package group7.tcss450.tacoma.uw.edu.overrun;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Random;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Test class for testing
 *
 * @author Ethan Rowell
 * @version Dec 6, 2016
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegistrationAndroidTest {

    /**
     * Rule for creating an activity.
     */
    @Rule
    public ActivityTestRule<SignInActivity> mActivityRule = new ActivityTestRule<>(
            SignInActivity.class);

    /**
     * Set up method before each test.
     */
    @Before
    public void setUp() {
        TestHelperMethods.clearSharedPreferences(mActivityRule.getActivity());
    }

    @Before
    public void tearDown() throws InterruptedException {
        Thread.sleep(5000);
    }

    /**
     * Tests the validity of the registration.
     */
    @Test
    public void testRegister_Valid() {
        onView(withId(R.id.register_button)).perform(click());

        Random random = new Random();
        //Generate an email address
        String email = "testEmail" + (random.nextInt(7) + 1)
                + (random.nextInt(8) + 1) + (random.nextInt(9) + 1)
                + (random.nextInt(100) + 1) + (random.nextInt(4) + 1)
                + "@blah.edu";
        onView(withId(R.id.reg_email)).perform(typeText(email));
        onView(withId(R.id.reg_password)).perform(typeText("blahblah1@"));
        onView(withId(R.id.reg_confirm_password)).perform(typeText("blahblah1@"));

        onView(withId(R.id.submit_registration_button)).perform(click());

        onView(withText("Successful account creation for: " + email))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Tests email validity during registration.
     */
    @Test
    public void testRegister_InvalidEmailError() {
        onView(withId(R.id.register_button)).perform(click());

        Random random = new Random();
        //Generate an email address
        String email = "testEmail" + (random.nextInt(7) + 1)
                + (random.nextInt(8) + 1) + (random.nextInt(9) + 1)
                + (random.nextInt(100) + 1) + (random.nextInt(4) + 1)
                + "@blahedu";
        onView(withId(R.id.reg_email)).perform(typeText(email));

        onView(withId(R.id.reg_email)).check(matches(withError("Email must be a valid email.")));
    }

    /**
     * Tests password is valid by containing a digit.
     */
    @Test
    public void testRegister_InvalidPassword_NoDigit() {
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.reg_password)).perform(typeText("blahblah@"));

        onView(withId(R.id.reg_password)).check(matches(withError("Password must contain a number.")));
    }

    /**
     * Tests password is valid by containing a special character.
     */
    @Test
    public void testRegister_InvalidPassword_NoSpecialChar() {
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.reg_password)).perform(typeText("blahblah11"));

        onView(withId(R.id.reg_password)).check(matches(withError("Password must contain a symbol.")));
    }

    /**
     * Tests password is valid by checking that both match.
     */
    @Test
    public void testRegister_InvalidPassword_PasswordsMatch() {
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.reg_password)).perform(typeText("blahblah1@"));
        onView(withId(R.id.reg_confirm_password)).perform(typeText("blahblah1@#"));

        onView(withId(R.id.reg_confirm_password)).check(matches(withError("Confirm Password does not match.")));
    }

    /**
     * Tests account doesn't already exists.
     */
    @Test
    public void testRegister_DuplicateRegistration() throws InterruptedException {
        onView(withId(R.id.register_button)).perform(click());

        // ensure this account is already in the DB or else this will fail.
        onView(withId(R.id.reg_email)).perform(typeText("blah@blah.com"));
        onView(withId(R.id.reg_password)).perform(typeText("blahblah1@"));
        onView(withId(R.id.reg_confirm_password)).perform(typeText("blahblah1@"));

        onView(withId(R.id.submit_registration_button)).perform(click());

        onView(withText("A user already exists with the email provided."))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Tests whether the error message contains the string expected.
     */
    private static Matcher<View> withError(final String expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                return editText.getError().toString().equals(expected);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}
