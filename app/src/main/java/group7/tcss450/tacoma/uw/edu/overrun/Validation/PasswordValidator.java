package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.view.View;
import android.widget.TextView;


/**
 * Validated passwords for text views.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class PasswordValidator extends TextValidator implements View.OnFocusChangeListener {
    private static int MIN_PASSWORD_LENGTH = 6;


    /**
     * @param textView The text view to watch.
     */
    public PasswordValidator(TextView textView) {
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
            if (text.length() < MIN_PASSWORD_LENGTH) {
                textView.setError(textView.getHint() + " must be " + MIN_PASSWORD_LENGTH + " characters or more.");
                valid = false;
            } else {
                // further validation
            }
        }
        return valid;
    }
}
