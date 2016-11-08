package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;

public class StartMenuActivity extends BaseActivity implements View.OnClickListener {

    private static MediaPlayer mMediaPlayer;
    private static SharedPreferences mSharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        Button op_button = (Button) findViewById(R.id.options_button);
        Button start_button = (Button) findViewById(R.id.start_button);
        Button sign_button = (Button) findViewById(R.id.login_button);

        op_button.setOnClickListener(this);
        start_button.setOnClickListener(this);
        sign_button.setOnClickListener(this);

        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);


        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(current_volume, current_volume);
            mMediaPlayer.start();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);
        mMediaPlayer.setVolume(current_volume, current_volume);

    }


    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.start_button:
                intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                break;

            case R.id.options_button:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                break;

            case R.id.login_button:
                intent = new Intent(this, SignInActivity.class);
                startActivity(intent);

                break;
        }
    }

    public void testLogout(View view) {
        signOut();
    }
}
