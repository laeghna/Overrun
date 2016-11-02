package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class StartMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
    }


    public void startGameClicked(View view) {
        // Crates an intent which will create a new activity PlayView.class
        Intent intent = new Intent(this, PlayView.class);

        startActivity(intent);
    }
}
