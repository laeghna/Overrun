package group7.tcss450.tacoma.uw.edu.overrun.Validation;

import android.view.View;
import android.widget.TextView;


public class UsernameValidator extends TextValidator implements View.OnFocusChangeListener {

    private static int MIN_USERNAME_LENGTH = 3;

    public UsernameValidator(TextView textView) {
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
            if (text.length() < MIN_USERNAME_LENGTH) {
                textView.setError(textView.getHint() + " must be " + MIN_USERNAME_LENGTH +  " characters or more.");
                valid = false;
            }
        }
        return valid;
    }
}
