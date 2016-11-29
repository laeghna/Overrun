package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import group7.tcss450.tacoma.uw.edu.overrun.Validation.PasswordTextWatcher;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;



@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PasswordValidatorTest {

    private EditText textView;
    private Context context;

    @Before
    public void testInit() {

        textView = new EditText(context);
    }

    @Test
    public void testPasswordValidation_Invalid_Short() {
        textView.setText("ac12@");
        PasswordTextWatcher passwordValidator = new PasswordTextWatcher(textView);
        assertFalse(passwordValidator.validate(textView));
    }

    @Test
    public void testPasswordValidation_Invalid_NoDigit() {
        textView.setText("abcdef@@");
        PasswordTextWatcher passwordValidator = new PasswordTextWatcher(textView);
        assertFalse(passwordValidator.validate(textView));
    }

    @Test
    public void testPasswordValidation_Invalid_NoSpecialChar() {
        textView.setText("acd1234");
        PasswordTextWatcher passwordValidator = new PasswordTextWatcher(textView);
        assertFalse(passwordValidator.validate(textView));
    }

    @Test
    public void testPasswordValidation_Valid() {
        textView.setText("abcd123##");
        PasswordTextWatcher passwordValidator = new PasswordTextWatcher(textView);
        assertTrue(passwordValidator.validate(textView));
    }

}
