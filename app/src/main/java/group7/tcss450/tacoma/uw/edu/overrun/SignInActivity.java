package group7.tcss450.tacoma.uw.edu.overrun;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void checkLoginStatus() {

        if (isLoggedIn()) {
            String userEmail = getSharedPreferences(getString(R.string.shared_prefs),
                                    Context.MODE_PRIVATE)
                                    .getString(getString(R.string.user_email), "");

            Toast.makeText(getApplicationContext(), "Signed in as: " + userEmail,
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, StartMenuActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }

}
