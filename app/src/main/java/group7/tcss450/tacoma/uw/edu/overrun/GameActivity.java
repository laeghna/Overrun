package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The activity that launches the Overrun game.
 */
public class GameActivity extends AppCompatActivity {

    /** The view for playing the game. */
    private PlayView mPlayView;

    /**
     * Summoned upon creation of an instance of the game.
     * @param savedInstanceState - the saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mPlayView = new PlayView(this);
        setContentView(mPlayView);
        mPlayView.run();
    }

    /**
     * Called when the game is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPlayView.pause();
    }

    /**
     * Called to resume the game from a paused state.
     */
    protected void onResume() {
        super.onResume();
        mPlayView.resumeGame();
    }
}
