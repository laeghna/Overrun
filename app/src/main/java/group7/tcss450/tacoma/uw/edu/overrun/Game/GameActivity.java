package group7.tcss450.tacoma.uw.edu.overrun.Game;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import group7.tcss450.tacoma.uw.edu.overrun.Listeners.ButtonListener;
import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * This is the activity for the actual game play.
 * It handles the game's lifecycle by calling GameView's
 * methods when prompted by the OS.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 04 December 2016
 */
public class GameActivity extends AppCompatActivity implements PropertyChangeListener{

    /** The game's play view where all images are drawn. */
    private PlayView mPlayView;

    private Button mPauseButton;

    /** The button for moving left. */
    private Button mLeftButton;

    /** The button for moving right. */
    private Button mRightButton;

    /** The buttons (left and right) for firing the weapon. */
    private Button mFireButton_L;
    private Button mFireButton_R;

    /** The timer delay for level 1. */
    private static final int SPAWN_INTERVAL_1 = 5000;  // 5 seconds

    /** The timer delay for level 2. */
    private static final int SPAWN_INTERVAL_2 = 4000;  // 4 seconds

    /** The timer delay for level 3. */
    private static final int SPAWN_INTERVAL_3 = 2000;  // 2 seconds

    /** The timer delay for spawning enemies. */
    private int spawnInterval;

    /** Handler for spawn timer. */
    private Handler spawnHandler;

    /** Shared preferences for the game. */
    private SharedPreferences mSharedPref;

    /** The layout. */
    private FrameLayout mLayout;

    /** The handler for the gameover process. */
    private Handler gameOverHandler;

    /** Dialog variable to be used for holding created dialogs. */
    private Dialog dialog;

    /**
     *     Boolean for controlling onResume method. It is needed to
     *     prevent the game from running in the background if the pause
     *     dialog is still up when returning to app from an external activity.
     */
    private boolean isResumimg;

    /** Boolean to prevent multiple dialogs being created by multiple calls to onPause. */
    private boolean hasPauseDialog;

    /**
     * To perform on creation of this Activity.
     * @param savedInstanceState the saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameOverHandler = new Handler();
        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        int choose_layout = mSharedPref.getInt("saved_controls", 0);

        //Initialize the play view object
        mPlayView = new PlayView(this);
        mPlayView.addPropertyChangeListener(this);
        mLayout = getLayout_1();

        if(choose_layout == 1) {
            mLayout = getLayout_2();
        } else if(choose_layout == 2) {
            mLayout = getLayout_3();
        }


        //add layout to ContentView
        setContentView(mLayout);

        setSpawnInterval();
        spawnHandler = new Handler();
        startSpawningTask();
        isResumimg = true;
        hasPauseDialog = false;

        // start game
        mPlayView.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSpawningTask();
        mPlayView.endGame();
    }



    protected void startSpawningTask() {
        spawnChecker.run();
    }

    protected void stopSpawningTask() {
        spawnHandler.removeCallbacks(spawnChecker);
    }

    private Runnable spawnChecker = new Runnable() {
        @Override
        public void run() {
            try {
                mPlayView.spawnZombie();

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                spawnHandler.postDelayed(spawnChecker, spawnInterval);
            }
        }
    };

    public void setSpawnInterval() {

        switch(mPlayView.getLevel()) {
            case 1: spawnInterval = SPAWN_INTERVAL_1;
                break;
            case 2: spawnInterval = SPAWN_INTERVAL_2;
                break;
            case 3: spawnInterval = SPAWN_INTERVAL_3;
                break;
            default: spawnInterval = SPAWN_INTERVAL_1;
                break;
        }
    }

    /**
     * Creates and returns the following UI layout :
     *  |-------------PAUSE-------------|
     *  |                               |
     *  |                               |
     *  *********************************
     *  | F                           F |
     *  | L                           R |
     *  ---------------------------------.
     * @return the UI layout.
     */
    public FrameLayout getLayout_1() {

        //The main layout for the game
        FrameLayout layout = new FrameLayout(this);

        //The center action bar
        LinearLayout actionBarCenter = new LinearLayout(this);
        actionBarCenter.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        actionBarCenter.setVerticalGravity(Gravity.TOP);

        mPauseButton = new Button(this);
        mPauseButton.setText(R.string.pause_button_txt);
        mPauseButton.setBackgroundResource(R.drawable.pause_button);
        mPauseButton.setTextColor(Color.GRAY);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onPause();
            }
        });

        actionBarCenter.addView(mPauseButton);

        // The left action bar
        LinearLayout actionBarLeft = new LinearLayout(this);
        actionBarLeft.setHorizontalGravity(Gravity.LEFT);
        actionBarLeft.setVerticalGravity(Gravity.BOTTOM);

        // The buttons layout for the left action bar
        LinearLayout buttonsLeft = new LinearLayout(this);
        buttonsLeft.setOrientation(LinearLayout.VERTICAL);

        // Create the buttons
        mLeftButton = new Button(this);
        mLeftButton.setBackgroundResource(R.drawable.move_button);
        mLeftButton.setTextColor(Color.GRAY);
        mLeftButton.setText(R.string.left_button_txt);
        mLeftButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveLeft();
            }
        }));

        mFireButton_L = new Button(this);
        mFireButton_L.setText(R.string.fire_button_txt);
        mFireButton_L.setBackgroundResource(R.drawable.fire_button);
        mFireButton_L.setOnTouchListener(new ButtonListener(0, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayView.fire();
            }
        }));


        //add the buttons to the proper layout
        buttonsLeft.addView(mFireButton_L);
        buttonsLeft.addView(mLeftButton);

        // The right action bar
        LinearLayout actionBarRight = new LinearLayout(this);
        actionBarRight.setHorizontalGravity(Gravity.RIGHT);
        actionBarRight.setVerticalGravity(Gravity.BOTTOM);

        // The buttons layout for the right action bar
        LinearLayout buttonsRight = new LinearLayout(this);
        buttonsRight.setOrientation(LinearLayout.VERTICAL);

        mRightButton = new Button(this);
        mRightButton.setText(R.string.right_button_txt);
        mRightButton.setBackgroundResource(R.drawable.move_button);
        mRightButton.setTextColor(Color.GRAY);
        mRightButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveRight();
            }
        }));

        mFireButton_R = new Button(this);
        mFireButton_R.setText(R.string.fire_button_txt);
        mFireButton_R.setBackgroundResource(R.drawable.fire_button);

        mFireButton_R.setOnTouchListener(new ButtonListener(0, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayView.fire();
            }
        }));

        buttonsRight.addView(mFireButton_R);
        buttonsRight.addView(mRightButton);

        //add the button layouts to the action bars
        actionBarLeft.addView(buttonsLeft);
        actionBarRight.addView(buttonsRight);

        if(mPlayView.getParent()!=null) {
            ((ViewGroup) mPlayView.getParent()).removeView(mPlayView);
        }

        layout.addView(mPlayView);
        layout.addView(actionBarCenter);
        layout.addView(actionBarLeft);
        layout.addView(actionBarRight);

        return layout;
    }

    /**
     * Creates and returns the following UI layout :
     *  |-------------PAUSE-------------|
     *  |                               |
     *  |                               |
     *  *********************************
     *  |                            F  |
     *  |                          L  R |
     *  ---------------------------------.
     * @return the UI layout.
     */
    public FrameLayout getLayout_2() {

        FrameLayout layout = new FrameLayout(this);

        LinearLayout actionBarCenter = new LinearLayout(this);
        actionBarCenter.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        actionBarCenter.setVerticalGravity(Gravity.TOP);

        mPauseButton = new Button(this);
        mPauseButton.setText(R.string.pause_button_txt);
        mPauseButton.setBackgroundResource(R.drawable.pause_button);
        mPauseButton.setTextColor(Color.GRAY);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });

        actionBarCenter.addView(mPauseButton);

        LinearLayout actionBarLow = new LinearLayout(this);
        actionBarLow.setOrientation(LinearLayout.VERTICAL);
        actionBarLow.setHorizontalGravity(Gravity.RIGHT);
        actionBarLow.setVerticalGravity(Gravity.BOTTOM);

        LinearLayout move_buttons_low = new LinearLayout(this);
        move_buttons_low.setOrientation(LinearLayout.HORIZONTAL);
        move_buttons_low.setVerticalGravity(Gravity.BOTTOM);
        move_buttons_low.setHorizontalGravity(Gravity.RIGHT);

        // Create the buttons
        mLeftButton = new Button(this);
        mLeftButton.setBackgroundResource(R.drawable.move_button);
        mLeftButton.setTextColor(Color.GRAY);
        mLeftButton.setText(R.string.left_button_txt);
        mLeftButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveLeft();
            }
        }));

        mRightButton = new Button(this);
        mRightButton.setText(R.string.right_button_txt);
        mRightButton.setBackgroundResource(R.drawable.move_button);
        mRightButton.setTextColor(Color.GRAY);
        mRightButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveRight();
            }
        }));
        move_buttons_low.addView(mLeftButton);
        move_buttons_low.addView(mRightButton);

        LinearLayout fire = new LinearLayout(this);
        fire.setOrientation(LinearLayout.HORIZONTAL);
        fire.setHorizontalGravity(Gravity.RIGHT);
        fire.setVerticalGravity(Gravity.BOTTOM);
        mFireButton_R = new Button(this);
        mFireButton_R.setText(R.string.fire_button_txt);
        mFireButton_R.setBackgroundResource(R.drawable.fire_button);
        mFireButton_R.setOnTouchListener(new ButtonListener(0, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayView.fire();
            }
        }));
        mFireButton_R.setWidth(mLeftButton.getWidth() + mRightButton.getWidth());
        fire.addView(mFireButton_R);

        actionBarLow.addView(fire);
        actionBarLow.addView(move_buttons_low);

        if(mPlayView.getParent()!=null) {
            ((ViewGroup) mPlayView.getParent()).removeView(mPlayView);
        }

        layout.addView(mPlayView);
        layout.addView(actionBarCenter);
        layout.addView(actionBarLow);

        return layout;
    }


    /**
     * Creates and returns the following UI layout :
     *  |-------------PAUSE-------------|
     *  |                               |
     *  |                               |
     *  *********************************
     *  | F                             |
     *  |L R                            |
     *  ---------------------------------.
     * @return the UI layout.
     */
    public FrameLayout getLayout_3() {

        FrameLayout layout = new FrameLayout(this);

        LinearLayout actionBarCenter = new LinearLayout(this);
        actionBarCenter.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        actionBarCenter.setVerticalGravity(Gravity.TOP);

        mPauseButton = new Button(this);
        mPauseButton.setText(R.string.pause_button_txt);
        mPauseButton.setBackgroundResource(R.drawable.pause_button);
        mPauseButton.setTextColor(Color.GRAY);
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();
            }
        });

        actionBarCenter.addView(mPauseButton);

        LinearLayout actionBarLow = new LinearLayout(this);
        actionBarLow.setOrientation(LinearLayout.VERTICAL);
        actionBarLow.setHorizontalGravity(Gravity.LEFT);
        actionBarLow.setVerticalGravity(Gravity.BOTTOM);

        LinearLayout move_buttons_low = new LinearLayout(this);
        move_buttons_low.setOrientation(LinearLayout.HORIZONTAL);
        move_buttons_low.setVerticalGravity(Gravity.BOTTOM);
        move_buttons_low.setHorizontalGravity(Gravity.LEFT);

        // Create the buttons
        mLeftButton = new Button(this);
        mLeftButton.setBackgroundResource(R.drawable.move_button);
        mLeftButton.setTextColor(getResources().getColor(R.color.gray));
        mLeftButton.setText(R.string.left_button_txt);
        mLeftButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveLeft();
            }
        }));

        mRightButton = new Button(this);
        mRightButton.setText(R.string.right_button_txt);
        mRightButton.setBackgroundResource(R.drawable.move_button);
        mRightButton.setTextColor(Color.GRAY);
        mRightButton.setOnTouchListener(new ButtonListener(10, 5, new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mPlayView.moveRight();
            }
        }));
        move_buttons_low.addView(mLeftButton);
        move_buttons_low.addView(mRightButton);

        LinearLayout fire = new LinearLayout(this);
        fire.setOrientation(LinearLayout.HORIZONTAL);
        fire.setHorizontalGravity(Gravity.LEFT);
        fire.setVerticalGravity(Gravity.BOTTOM);
        mFireButton_R = new Button(this);
        mFireButton_R.setText(R.string.fire_button_txt);
        mFireButton_R.setBackgroundResource(R.drawable.fire_button);
        mFireButton_R.setOnTouchListener(new ButtonListener(0, 0, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayView.fire();
            }
        }));
        mFireButton_R.setWidth(mLeftButton.getWidth() + mRightButton.getWidth());
        fire.addView(mFireButton_R);

        actionBarLow.addView(fire);
        actionBarLow.addView(move_buttons_low);

        if(mPlayView.getParent()!=null) {
            ((ViewGroup) mPlayView.getParent()).removeView(mPlayView);
        }

        layout.addView(mPlayView);
        layout.addView(actionBarCenter);
        layout.addView(actionBarLow);

        return layout;
    }

    /**
     * Pauses the game.
     */
    @Override
    protected void onPause() {

        super.onPause();
        if (!hasPauseDialog && !mPlayView.getIsGameOver()) {

            stopSpawningTask();
            isResumimg = false;
            hasPauseDialog = true;
            mPlayView.pauseGame();
            AlertDialog.Builder db = new AlertDialog.Builder(GameActivity.this);
            db.setMessage(R.string.pause_dialog)
                    .setPositiveButton(R.string.resume_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface di, int id) {
                            isResumimg = true;
                            hasPauseDialog = false;
                            onResume();
                        }
                    });
            dialog = db.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * Called when the game has ended. Pops up a dialog that gives
     * the user options to either play again or quit to the main menu.
     */
    protected void onGameOver() {
        stopSpawningTask();
        mPlayView.endGame();
        Looper.prepare();
        gameOverHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder dialog_builder = new AlertDialog.Builder(GameActivity.this);
                dialog_builder.setMessage(R.string.game_over_text)
                        .setPositiveButton(R.string.play_again_text, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface d, int id) {
                                //do something
                                try {
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);

                                } catch(Exception e) {

                                }
                            }
                        }).setNegativeButton(R.string.exit_button, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface d, int id) {
                                //do something
                                try {

                                    mLayout.removeAllViews();
                                    finish();

                                } catch (Exception e) {

                                }
                            }
                });
                dialog = dialog_builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    /**
     * Called when the game is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!isResumimg) {

            //mPlayView.pauseGame();

        } else {

            startSpawningTask();
            mPlayView.resumeGame();
        }
    }

    /**
     * Property change method is triggered when a property change
     * is fired from the playview class. This will be triggered when the
     * game is over.
     * @param evt the event that triggered the property change.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        Boolean newVal = (Boolean) evt.getNewValue();
        if(newVal) {

            onGameOver();
        }
    }

    /**
     * Gets the user's email.
     * @return the email.
     */
    public String getEmail() {
        return mSharedPref.getString("user_email", "default");
    }

}