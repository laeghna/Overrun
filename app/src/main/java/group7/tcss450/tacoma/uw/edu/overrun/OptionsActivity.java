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



        Spinner mySpinner = (Spinner) findViewById(R.id.diff_spinner);

        mySpinner.setSelection(current_difficulty - 1);

    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.cancel_button_options:
                intent = new Intent(this, StartMenuActivity.class);
                startActivity(intent);
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


                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.putInt(getString(R.string.saved_difficulty_setting), difficulty);
                editor.commit();


                Toast.makeText(this
                        , "Difficulty set to: " + text,
                        Toast.LENGTH_SHORT) .show();
                intent = new Intent(this, StartMenuActivity.class);
                startActivity(intent);
                break;

        }
    }
}
