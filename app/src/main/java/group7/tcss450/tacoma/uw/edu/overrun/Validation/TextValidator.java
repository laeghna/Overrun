package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Validates input fields after the text is changed.
 * Source: https://goo.gl/ilB6rA
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
abstract class TextValidator implements TextWatcher {

    /**
     * The textview to validate.
     */
    private final TextView textView;

    TextValidator(TextView textView) {
        this.textView = textView;
    }

    /**
     * Validation logic is implemented through this method.
     * @param textView The text view to validate.
     * @return Whether the text is valid or not.
     */
    public abstract boolean validate(TextView textView);

    /**
     * Returns the instance of the textfield assigned to this validator.
     * @return a text view
     */
    public TextView getMyTextView() {
        return textView;
    }

    @Override
    final public void afterTextChanged(Editable s) {
        validate(textView);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { }
}
