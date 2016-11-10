package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import group7.tcss450.tacoma.uw.edu.overrun.SignIn.SignInActivity;


/**
 * This is the activity for the actual initial start menu.
 * It handles the game's lifecycle by calling StartMenu's
 * methods when prompted by the OS. This activity provides
 * the User with the ability to start a new game, move to the
 * options menu, and move to the login/register menu.
 *
 * @author Andrew Merz
 * @version 8 Nov 2016
 */
public class StartMenuActivity extends BaseActivity implements View.OnClickListener {

    private static MediaPlayer mMediaPlayer;
    private static SharedPreferences mSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);


        // Setting onClickListeners for each button on layout.
        Button op_button = (Button) findViewById(R.id.options_button);
        Button start_button = (Button) findViewById(R.id.start_button);
        Button sign_button = (Button) findViewById(R.id.login_button);

        op_button.setOnClickListener(this);
        start_button.setOnClickListener(this);
        sign_button.setOnClickListener(this);

        // Getting the current volume setting in system preferences.
        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);


        // Creating a MediaPlayer object if the member variable is
        // currently null. Set the music to the theme music, and set it to loop.
        // Starts the music when this activity is created.
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(current_volume, current_volume);
            mMediaPlayer.start();
        }
    }

    /**
     * The onResume callback method for this activity adjusts
     * the background theme music volume depending on settings
     * that were saved in the Options Menu.
     */
    @Override
    protected void onResume() {
        super.onResume();


        boolean loggedIn = mSharedPref.getBoolean(getString(R.string.logged_in), false);
        // Check if the user is logged in then
        // change the Sign in button.
        if (loggedIn) {
            Button sign_button = (Button) findViewById(R.id.login_button);
            sign_button.setText("Log out");
        }

        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);
        mMediaPlayer.setVolume(current_volume, current_volume);

        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.start_button:
                mMediaPlayer.pause();
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;

            // Starts a new intent to move to the OptionsMenu activity.
            case R.id.options_button:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                break;

            // Starts a new intent to move to the SignIn activity.
            case R.id.login_button:

                boolean loggedIn = mSharedPref.getBoolean(getString(R.string.logged_in), false);
                // Check if the user is logged in then
                // change the Sign in button.
                if (loggedIn) {
                    Toast.makeText(v.getContext(), mSharedPref.getString(getString(R.string.user_email), "") +
                                    " logged out.."
                            , Toast.LENGTH_LONG)
                            .show();


                    Button sign_button = (Button) findViewById(R.id.login_button);
                    sign_button.setText("Sign in");

                    mSharedPref.edit()
                            .putBoolean(getString(R.string.logged_in), false)
                            .apply();
                    mSharedPref.edit()
                            .putString(getString(R.string.user_email), "")
                            .apply();
                } else {
                    intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }

    // TODO: logout button needs to be implemented.
    public void testLogout(View view) {
        signOut();
    }
}
