package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This is the activity for the Options/Settings menu for the application.
 * It handles the game's lifecycle by calling OptionActivitys's
 * methods when prompted by the OS. This activity provides
 * the User with the ability to change the current difficulty setting
 * of the application, along with the current volume level  of
 * the music and sound effects.
 *
 * @author Andrew Merz
 * @author Lisa Taylor
 * @version 06 December 2016
 */
public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mSharedPref;
    private Spinner mDiffSpinner;
    private Spinner mControlsSpinner;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        mDiffSpinner = (Spinner) findViewById(R.id.diff_spinner);
        mControlsSpinner = (Spinner) findViewById(R.id.control_spinner);

        Button cancel_button = (Button) findViewById(R.id.cancel_button_options);
        Button ok_button = (Button) findViewById(R.id.ok_button_options);

        cancel_button.setOnClickListener(this);
        ok_button.setOnClickListener(this);

        // Set the settings to the saved settings in Preferences.
        int current_difficulty = mSharedPref.getInt(
                getString(R.string.saved_difficulty_setting), 1);

        int current_controls = mSharedPref.getInt("saved_controls", 1);

        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);

        // Set volume to an integer to properly display on Slider.
        int volume_int = (int) (current_volume * 100);

        Spinner mydiffSpinner = (Spinner) findViewById(R.id.diff_spinner);
        Spinner myControlsSpinner = (Spinner) findViewById(R.id.control_spinner);

        SeekBar volumeBar = (SeekBar) findViewById(R.id.volume_bar);
        volumeBar.setProgress(volume_int);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                mMediaPlayer.setVolume(updateVolume(),updateVolume());
            }
        });
        mydiffSpinner.setSelection(current_difficulty - 1);
        myControlsSpinner.setSelection(current_controls);

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(current_volume, current_volume);
            int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);
            mMediaPlayer.seekTo(music_position);
            mMediaPlayer.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int music_position = mSharedPref.getInt(getString(R.string.music_position), 0);
        float current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.dark_theme);

            mMediaPlayer.setVolume(current_volume,current_volume);
            mMediaPlayer.seekTo(music_position);
            mMediaPlayer.start();
        }

        else if (!mMediaPlayer.isPlaying()) {

            mMediaPlayer.setVolume(current_volume,current_volume);
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
            mMediaPlayer.release();
        }

        if (mMediaPlayer != null) {

            mMediaPlayer = null;
        }

        this.finish();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // If cancel button clicked, exit options without saving
            case R.id.cancel_button_options:
                this.finish();
                break;

            // If the OK button is clicked, save all settings and return
            // to the StartMenuActivity.
            case R.id.ok_button_options:

                // Get the current values of the settings.
                String diffText = mDiffSpinner.getSelectedItem().toString();
                String controlsText = mControlsSpinner.getSelectedItem().toString();
                float volume_level = updateVolume();
                int controls = updateControls(controlsText);
                int difficulty = updateDifficulty(diffText);

                // Save the current settings to Preferences.
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putInt(getString(R.string.saved_difficulty_setting), difficulty);
                editor.putFloat(getString(R.string.saved_volume_setting), volume_level);
                editor.putInt(getString(R.string.saved_controls_setting), controls);
                editor.commit();

                // Provide feedback to User to show that
                // the settings have been changed and saved.
                Toast.makeText(this
                        , "Difficulty set to: " + diffText + "\nVolume set to: " +
                                (int)(volume_level * 100) + "%",
                        Toast.LENGTH_SHORT) .show();

                finish();
                break;
        }
    }

    /**
     * Updates the current difficulty level based off
     * selected text of difficulty Spinner.
     *
     * @param theText String of selected difficulty.
     * @return int value based off selected difficulty {1, 2, 3}
     */
    private int updateDifficulty(String theText) {

        int difficulty = 0;

        switch (theText) {
            case "Slow and Steady":
                difficulty = 1;
                break;
            case "Food for Thought":
                difficulty = 2;
                break;
            case "Brain Dead":
                difficulty = 3;
                break;
        }

        return difficulty;
    }

    private int updateControls(String theText) {

        int controls = 0;

        switch (theText) {
            case "Control Layout One":
                controls = 0;
                break;
            case "Control Layout Two":
                controls = 1;
                break;
            case "Control Layout Three":
                controls = 2;
                break;
        }

        return controls;
    }

    /**
     * Updates the current volume level based
     * on the position of the volume slider.
     *
     * @return the slider postion as a float from 0 - 1
     */
    private float updateVolume() {
        SeekBar volumeBar = (SeekBar) findViewById(R.id.volume_bar);
        int currentInt = volumeBar.getProgress();
        float volume_level = (float) currentInt / 100;
        return volume_level;
    }
}
