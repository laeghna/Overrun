package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.view.View;
import android.widget.TextView;

import static android.R.attr.password;


/**
 * Validated passwords for text views.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class PasswordValidator extends TextValidator implements View.OnFocusChangeListener {

    /**
     * Minimum password length.
     */
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
        boolean foundDigit = false, foundSymbol = false;

        if (text.isEmpty()) {
            textView.setError(textView.getHint() + " is required.");
            return false;
        } else {
            if (text.length() < MIN_PASSWORD_LENGTH) {
                textView.setError(textView.getHint() + " must be " + MIN_PASSWORD_LENGTH + " characters or more.");
                return false;
            } else {
                for (int i = 0 ; i < text.length(); i++) {
                    if (Character.isDigit(text.charAt(i)))
                        foundDigit = true;
                    if (!Character.isLetterOrDigit(text.charAt(i)))
                        foundSymbol = true;
                }

                if (!foundDigit) {
                    textView.setError(textView.getHint() + " must contain a number.");
                    return false;
                } else if (!foundSymbol) {
                    textView.setError(textView.getHint() + " must contain a symbol.");
                    return false;
                }
            }
        }
        return true;
    }
}
