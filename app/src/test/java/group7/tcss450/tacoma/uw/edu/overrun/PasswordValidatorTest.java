package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import group7.tcss450.tacoma.uw.edu.overrun.Validation.PasswordValidator;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.RuntimeEnvironment.application;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class PasswordValidatorTest {

    private EditText textView;

    @Before
    public void testInit() {
        textView = new EditText(application);
    }

    @Test
    public void testPasswordValidation_Invalid_Short() {
        textView.setText("ac12@");
        PasswordValidator passwordValidator = new PasswordValidator(textView);
        assertFalse(passwordValidator.validate(textView));
    }

    @Test
    public void testPasswordValidation_Invalid_NoDigit() {
        textView.setText("abcdef@@");
        PasswordValidator passwordValidator = new PasswordValidator(textView);
        assertFalse(passwordValidator.validate(textView));
    }

    @Test
    public void testPasswordValidation_Invalid_NoSpecialChar() {
        textView.setText("acd1234");
        PasswordValidator passwordValidator = new PasswordValidator(textView);
        assertFalse(passwordValidator.validate(textView));
    }

    @Test
    public void testPasswordValidation_Valid() {
        textView.setText("abcd123##");
        PasswordValidator passwordValidator = new PasswordValidator(textView);
        assertTrue(passwordValidator.validate(textView));
    }

}
