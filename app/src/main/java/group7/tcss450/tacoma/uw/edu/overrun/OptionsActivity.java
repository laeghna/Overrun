package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences mSharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        Button cancel_button = (Button) findViewById(R.id.cancel_button_options);
        Button ok_button = (Button) findViewById(R.id.ok_button_options);

        cancel_button.setOnClickListener(this);
        ok_button.setOnClickListener(this);


        int current_difficulty = mSharedPref.getInt(
                getString(R.string.saved_difficulty_setting), 1);

        double current_volume = mSharedPref.getFloat(
                getString(R.string.saved_volume_setting), 1);

        System.out.println(current_volume);

        int volume_int = (int) (current_volume * 100);

        System.out.println(volume_int);
        Spinner mySpinner = (Spinner) findViewById(R.id.diff_spinner);
        SeekBar volumeBar = (SeekBar) findViewById(R.id.volume_bar);

        volumeBar.setProgress(volume_int);
        mySpinner.setSelection(current_difficulty - 1);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cancel_button_options:
                this.finish();
                break;

            case R.id.ok_button_options:

                int difficulty = 0;
                Spinner mySpinner=(Spinner) findViewById(R.id.diff_spinner);
                String text = mySpinner.getSelectedItem().toString();

                switch (text) {
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


                float volume_level = updateVolume();

                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putInt(getString(R.string.saved_difficulty_setting), difficulty);
                editor.putFloat(getString(R.string.saved_volume_setting), volume_level);
                editor.commit();

                Toast.makeText(this
                        , "Difficulty set to: " + text + "\nVolume set to: " +
                                (int)(volume_level * 100) + "%",
                        Toast.LENGTH_SHORT) .show();
                finish();
                break;

        }
    }

    private float updateVolume() {
        SeekBar volumeBar = (SeekBar) findViewById(R.id.volume_bar);

        int currentInt = volumeBar.getProgress();

        float volume_level = (float) currentInt / 100;

        return volume_level;


    }
}
