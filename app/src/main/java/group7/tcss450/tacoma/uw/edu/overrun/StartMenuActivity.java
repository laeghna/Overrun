package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * This is the main activity that runs when the game app is started.
 * It provides buttons to either play the game or view other options.
 *
 * @author Lisa Taylor
 * @author Leslie Pedro
 * @version 23 Oct 2016
 */
public class StartMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
    }


    public void startGameClicked(View view) {
        // Creates an intent which will create a new activity PlayView.class
        Intent intent = new Intent(this, PlayView.class);

        startActivity(intent);
    }
}
