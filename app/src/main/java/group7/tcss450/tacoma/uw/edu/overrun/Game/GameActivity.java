package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

    /** The buttons (left and right) for firing the weapon. */
    private Button mFireButton_L;
    private Button mFireButton_R;

    private SharedPreferences mSharedPref;


    /**
     * To perform on creation of this Activity.
     * @param savedInstanceState the saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPref = getSharedPreferences(
                getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        int choose_layout = mSharedPref.getInt("saved_controls", 0);

//        int choose_layout = 0;
        //Initialize the play view object
        mPlayView = new PlayView(this);
        FrameLayout layout = getLayout_1();

        if(choose_layout == 1) {
            layout = getLayout_2();
        } else if(choose_layout == 2) {
            layout = getLayout_3();
        }


        //add layout to ContentView
        setContentView(layout);

        // start game
        mPlayView.run();
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
        mPauseButton.setTextColor(getResources().getColor(R.color.gray));
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
        mLeftButton.setTextColor(getResources().getColor(R.color.gray));
//        mLeftButton = (Button) findViewById(R.id.left_button);
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
        mRightButton.setTextColor(getResources().getColor(R.color.gray));
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
        mPauseButton.setTextColor(getResources().getColor(R.color.gray));
//        mPauseButton = (Button) findViewById(R.id.pause_button);
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
        mLeftButton.setTextColor(getResources().getColor(R.color.gray));
//        mLeftButton = (Button) findViewById(R.id.left_button);
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
        mRightButton.setTextColor(getResources().getColor(R.color.gray));
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
        mPauseButton.setTextColor(getResources().getColor(R.color.gray));
//        mPauseButton = (Button) findViewById(R.id.pause_button);
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
//        mLeftButton = (Button) findViewById(R.id.left_button);
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
        mRightButton.setTextColor(getResources().getColor(R.color.gray));
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
