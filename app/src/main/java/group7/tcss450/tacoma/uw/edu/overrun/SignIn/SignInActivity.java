package group7.tcss450.tacoma.uw.edu.overrun.SignIn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.StartMenuActivity;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            showLoginFragment();

        }

        Button button = (Button) findViewById(R.id.register_button);

        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRegistrationFragment();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.sign_in_button:
//                signIn();
//                break;
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }


    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void showRegistrationFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegistrationFragment())
                .addToBackStack(null)
                .commit();
    }

    private void goToStartMenu() {
        String userEmail = getSharedPreferences(getString(R.string.shared_prefs),
                Context.MODE_PRIVATE)
                .getString(getString(R.string.user_email), "");

        Toast.makeText(getApplicationContext(), "Signed in as: " + userEmail,
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, StartMenuActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Checks the login status of the user. If logged in already, it will route the user
     * to the startMenu
     */
    private void checkLoginStatus() {

        if (this.isLoggedIn()) {
            goToStartMenu();
        }
    }
}
