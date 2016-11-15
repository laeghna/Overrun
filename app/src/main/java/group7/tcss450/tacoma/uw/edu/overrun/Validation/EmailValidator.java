package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.view.View;
import android.widget.TextView;


/**
 * Validator for the email.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class EmailValidator extends TextValidator implements View.OnFocusChangeListener {

    /**
     * Minimum length for an email.
     */
    private static int MIN_EMAIL_LENGTH = 3;

    public EmailValidator(TextView textView) {
        super(textView);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            validate((TextView) v);
        }
    }

    @Override
    public boolean validate(TextView textView) {
        String text = textView.getText().toString();
        boolean valid = true;

        if (text.isEmpty()) {
            textView.setError(textView.getHint() + " is required.");
            valid = false;
        } else {
            if (text.length() < MIN_EMAIL_LENGTH) {
                textView.setError(textView.getHint() + " must be " + MIN_EMAIL_LENGTH +  " characters or more.");
                valid = false;
            }
        }
        return valid;
    }
}
