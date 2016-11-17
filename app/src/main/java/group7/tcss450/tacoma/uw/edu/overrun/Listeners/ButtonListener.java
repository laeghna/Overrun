package group7.tcss450.tacoma.uw.edu.overrun.Listeners;


import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * ButtonListener class interprets button clicks into real game actions.
 * @author : Leslie Pedro
 */

public class ButtonListener implements View.OnTouchListener {

    /** The handler for the button listener. */
    private Handler mHandler = new Handler();

    /** The delay from the initial button press to the first action. */
    private int mStartDelay;

    /** Delay between subsequent actions. */
    private final int mRunDelay;

    /** The on click listener for the buttons. */
    private View.OnClickListener mButtonClickListener;

    /** A runnable for repeating actions while button is held. */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, mRunDelay);
            mButtonClickListener.onClick(mView);
        }
    };

    /** The view corresponding with this action. */
    private View mView;

    /**
     * Public constructor for ButtonListener.
     * @param startDelay the start delay.
     * @param runDelay the delay in between actions.
     * @param listen the listener for the button.
     */
    public ButtonListener(int startDelay, int runDelay, View.OnClickListener listen) {
        if(listen == null) {
            throw new IllegalArgumentException("Cannot run null value");
        }
        if (startDelay < 0 || runDelay < 0) {
            throw new IllegalArgumentException("Delay value must be > 0");
        }
        mStartDelay = startDelay;
        mRunDelay = runDelay;
        mButtonClickListener = listen;
    }


    /**
     * Determines the action to be completed by the event that triggered this listener.
     * @param v the view
     * @param event the event triggering this listener.
     * @return true if the event completes, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, mStartDelay);
                mView = v;
                v.setPressed(true);
                mButtonClickListener.onClick(mView);
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandler.removeCallbacks(mRunnable);
                mView.setPressed(false);
                mView = null;
                return true;
        }
        return false;
    }
}
