package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Validated passwords for text views.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class PasswordTextWatcher extends TextValidator implements View.OnFocusChangeListener {

    /**
     * Minimum password length.
     */
    private static int MIN_PASSWORD_LENGTH = 6;

    /**
     * List of EditTexts to ensure this field matches.
     */
    private List<EditText> otherPasswordFields;

    /**
     * Ensures validation doesn't occur until after it gains focus and then
     * focus is lost.
     */
    private boolean touched = false;

    /**
     * @param textView The text view to watch.
     */
    public PasswordTextWatcher(TextView textView) {
        super(textView);
        otherPasswordFields = new ArrayList<>();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) touched = true;

        if (!hasFocus && touched) {
            validate((TextView) v);
        }
    }

    /**
     * Adds another password field to check against to ensure they match.
     * @param passwordField The password field to check against.
     */
    public void addPasswordField(EditText passwordField) {
        otherPasswordFields.add(passwordField);
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

                // loops through all textviews associated with this validator.
                for (EditText textField : otherPasswordFields) {
                    if (!getMyTextView().getText().toString()
                            .equals(textField.getText().toString())) {
                        textView.setError(textView.getHint() + " does not match.");
                    }
                }
            }
        }
        return true;
    }
}
