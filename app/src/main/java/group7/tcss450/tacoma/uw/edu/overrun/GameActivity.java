package group7.tcss450.tacoma.uw.edu.overrun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This is the activity for the actual game play.
 * It handles the game's lifecycle by calling GameView's
 * methods when prompted by the OS.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 1 Nov 2016
 */
public class GameActivity extends AppCompatActivity {

    //The game's play view
    private PlayView mPlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Initialize the play view object
        mPlayView = new PlayView(this);

        //add play view to ContentView
        setContentView(mPlayView);

        mPlayView.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayView.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayView.resumeGame();
    }
}
