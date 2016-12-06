package group7.tcss450.tacoma.uw.edu.overrun.SignIn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import group7.tcss450.tacoma.uw.edu.overrun.BaseActivity;
import group7.tcss450.tacoma.uw.edu.overrun.R;
import group7.tcss450.tacoma.uw.edu.overrun.StartMenuActivity;

/**
 * Activity that encapsulates the login and registration for the user.
 *
 * @author Ethan Rowell
 * @version 9 Nov 2016
 */
public class SignInActivity extends BaseActivity {

    public CallbackManager callbackManager;
    private MediaPlayer mMediaPlayer;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);


        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        ButterKnife.bind(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            showLoginFragment();
        }

        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);

        mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(current_volume, current_volume);
        int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);
        mMediaPlayer.seekTo(music_position);
        mMediaPlayer.start();
    }

    @Override
    public void onResume() {
        super.onResume();

            int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);
            float current_volume = mSharedPref.getFloat(
                    getString(R.string.saved_volume_setting), 1);

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
            mMediaPlayer.setVolume(current_volume, current_volume);
            mMediaPlayer.seekTo(music_position);
            mMediaPlayer.start();
        }

        else if (!mMediaPlayer.isPlaying()) {

            mMediaPlayer.setVolume(current_volume, current_volume);
            mMediaPlayer.seekTo(music_position);
            mMediaPlayer.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer.isPlaying()) {
            mSharedPref.edit()
                    .putInt(getString(R.string.music_position), mMediaPlayer.getCurrentPosition())
                    .apply();

            mMediaPlayer.pause();

        }

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Optional @OnClick(R.id.register_button)
    void showRegFrag() {
        showRegistrationFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginStatus();
    }

    /**
     * Shows the login fragment.
     */
    private void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    /**
     * Shows the registration fragment.
     */
    private void showRegistrationFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RegistrationFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Transitions to the StartMenuActivity.
     */
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
