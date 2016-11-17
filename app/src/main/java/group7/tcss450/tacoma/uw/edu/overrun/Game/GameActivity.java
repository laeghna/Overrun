package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import group7.tcss450.tacoma.uw.edu.overrun.Listeners.ButtonListener;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * This is the activity for the actual game play.
 * It handles the game's lifecycle by calling GameView's
 * methods when prompted by the OS.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 4 Nov 2016
 */
public class GameActivity extends AppCompatActivity {

    /** The game's play view where all images are drawn. */
    private PlayView mPlayView;

    private Button mPauseButton;

    /** The button for moving left. */
    private Button mLeftButton;

    /** The button for moving right. */
    private Button mRightButton;

    /** The button for firing the weapon. */
    private Button mFireButton;

    /**
     * To perform on creation of this Activity.
     * @param savedInstanceState the saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //The main layout for the game
        FrameLayout layout = new FrameLayout(this);

        // The left action bar
        LinearLayout actionBarLeft = new LinearLayout(this);
        actionBarLeft.setHorizontalGravity(Gravity.LEFT);
        actionBarLeft.setVerticalGravity(Gravity.BOTTOM);

        // The buttons layout for the left action bar
        LinearLayout buttonsLeft = new LinearLayout(this);
        buttonsLeft.setOrientation(LinearLayout.VERTICAL);

        // The right action bar
        LinearLayout actionBarRight = new LinearLayout(this);
        actionBarRight.setHorizontalGravity(Gravity.RIGHT);
        actionBarRight.setVerticalGravity(Gravity.BOTTOM);

        // The buttons layout for the right action bar
        LinearLayout buttonsRight = new LinearLayout(this);
        buttonsRight.setOrientation(LinearLayout.VERTICAL);

        //Initialize the play view object
        mPlayView = new PlayView(this);

        // Create the buttons
        mLeftButton = new Button(this);
        mLeftButton.setText("LEFT");
        mLeftButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveLeft();
            }
        }));

        mRightButton = new Button(this);
        mRightButton.setText("RIGHT");
        mRightButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveRight();
            }
        }));
        mFireButton = new Button(this);
        mFireButton.setText("FIRE!");
        mFireButton.setOnTouchListener(new ButtonListener(0, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayView.fire();
            }
        }));

        mPauseButton = new Button(this);
        mPauseButton.setText("PAUSE");
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });

        //add the buttons to the proper layout
        buttonsLeft.addView(mPauseButton);
        buttonsLeft.addView(mLeftButton);

        buttonsRight.addView(mFireButton);
        buttonsRight.addView(mRightButton);

        //add the button layouts to the action bars
        actionBarLeft.addView(buttonsLeft);
        actionBarRight.addView(buttonsRight);

        // add game view, left action bar, and right action bar to the main frame
        layout.addView(mPlayView);
        layout.addView(actionBarLeft);
        layout.addView(actionBarRight);

        //add layout to ContentView
        setContentView(layout);
        // start game
        mPlayView.run();
    }

    /**
     * Called when the game is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPlayView.pauseGame();
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        dialog_builder.setMessage(R.string.pause_dialog)
                .setPositiveButton(R.string.resume_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int id) {
                        onResume();
                    }
                });
        AlertDialog dialog = dialog_builder.create();
        dialog.show();
        //TODO: finish
    }

    /**
     * Called when the game is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mPlayView.resumeGame();
    }
}
