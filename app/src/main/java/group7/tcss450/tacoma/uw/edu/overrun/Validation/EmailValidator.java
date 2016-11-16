package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.view.View;
import android.widget.TextView;

import java.util.regex.Pattern;


/**
 * Validator for the email.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class EmailValidator extends TextValidator implements View.OnFocusChangeListener {

    /**
     * Email validation pattern.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

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
            if (!EMAIL_PATTERN.matcher(text).matches()) {
                textView.setError(textView.getHint() + " must be a valid email.");
                valid = false;
            }
        }
        return valid;
    }
}
