package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import group7.tcss450.tacoma.uw.edu.overrun.Game.*;

import group7.tcss450.tacoma.uw.edu.overrun.Leaderboard.LeaderboardActivity;
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

    private MediaPlayer mMediaPlayer;
    private SharedPreferences mSharedPref;
    private ShareDialog shareDialog;
    private Button fbShareButton;
    private CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_start_menu);


        shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();


        fbShareButton = (Button) findViewById(R.id.share_btn);

        fbShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Overrun Highscore!")
                            .setContentDescription("Score: " +
                                    mSharedPref.getString(getString(R.string.recient_high_score), "0"))
                            .setContentUrl(Uri.parse("https://developers.facebook.com"))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        boolean loggedIn = mSharedPref.getBoolean(getString(R.string.logged_in), false);
        // Check if the user is logged in then
        // change the Sign in button.
        Button sign_button = (Button) findViewById(R.id.login_button);
        if (loggedIn) {

            sign_button.setText("Log out");
        }
        else {
            sign_button.setText("Sign in");
        }

        // Setting onClickListeners for each button on layout.
        Button op_button = (Button) findViewById(R.id.options_button);
        Button start_button = (Button) findViewById(R.id.start_button);
        sign_button = (Button) findViewById(R.id.login_button);
        Button leaderboard_button = (Button) findViewById(R.id.leaderboard_button);

        op_button.setOnClickListener(this);
        start_button.setOnClickListener(this);
        sign_button.setOnClickListener(this);
        leaderboard_button.setOnClickListener(this);

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

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        Button sign_button = (Button) findViewById(R.id.login_button);
        if (loggedIn) {

            sign_button.setText("Log out");
        }
        else {
            sign_button.setText("Sign in");
        }

        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);


        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
            int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);

            mMediaPlayer.setVolume(current_volume, current_volume);
            mMediaPlayer.seekTo(music_position);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }

        else if (!mMediaPlayer.isPlaying()) {
            int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);

            mMediaPlayer.setVolume(current_volume, current_volume);
            mMediaPlayer.seekTo(music_position);
            mMediaPlayer.setLooping(true);
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
            mMediaPlayer.stop();
            mMediaPlayer.release();



        }

        if (mMediaPlayer != null) {
            mMediaPlayer = null;
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

                    mSharedPref.edit()
                            .putString(getString(R.string.recient_high_score), "0")
                            .apply();

                    LoginManager.getInstance().logOut();
                } else {
                    intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.leaderboard_button:
                intent = new Intent(this, LeaderboardActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void testLogout(View view) {
        signOut();
    }
}
