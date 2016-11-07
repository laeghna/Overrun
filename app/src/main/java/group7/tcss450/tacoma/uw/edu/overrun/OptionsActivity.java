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

public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Button cancel_button = (Button) findViewById(R.id.cancel_button_options);
        Button ok_button = (Button) findViewById(R.id.ok_button_options);

        cancel_button.setOnClickListener(this);
        ok_button.setOnClickListener(this);
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

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_difficulty_setting), difficulty);
                editor.commit();
                
                intent = new Intent(this, StartMenuActivity.class);
                startActivity(intent);
                break;

        }
    }
}
