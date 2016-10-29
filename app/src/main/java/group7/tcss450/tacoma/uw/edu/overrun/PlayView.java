package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.WindowManager;

/** This class is intended for use in the game Overrun. A fun and fast-paced survival
 * game. This class holds the view for the in-app gameplay.
 *
 * Author: Leslie Pedro
 */
public class PlayView extends SurfaceView implements Runnable{

    /** A volatile boolean for controlling multithreaded play. */
    volatile boolean mIsPlaying; // True when game is in-play, false otherwise

    /** The thread for the playing the game. */
    private Thread mGameThread;

    /** The survivor (player character) of the game. */
    private Survivor mSurvivor;

    /** The paint object for use in drawing. */
    private Paint mPaintBrush;

    /** The canvas (background) object to draw on. */
    private Canvas mBackground;

    /** The holder for this surface view. */
    private SurfaceHolder mHolder;

    

    /** Constructor for the PlayView class. */
    public PlayView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point screenSize = new Point();
        d.getSize(screenSize);

        // create survivor object
        mSurvivor = new Survivor(context, screenSize);
        // create paint object for rendering
        mPaintBrush = new Paint();
        //create holder for view
        mHolder = getHolder();


    }

    /**
     * Game control method to start playing an instance of the game.
     * Runs the game on loop until mIsPlaying is false.
     */
    @Override
    public void run() {
        // game control loop: while the user is playing continue to update the view
        while(mIsPlaying) {
            //update and draw frame
            updateFrame();
            drawFrame();
            //update the frame controls
            framesPerSecond();
        }

    }

    /** Updates the frame for the PlayView. */
    private void updateFrame(){
        drawFrame();
    }

    /** Draws the frame for the PlayView. */
    private void drawFrame() {
        // draw all graphics
        // check holder
        if( mHolder.getSurface().isValid()) {
            mBackground = mHolder.lockCanvas(); // lock the background for drawing
            mBackground.drawColor(Color.BLACK); // color the background black
            mBackground.drawBitmap(mSurvivor.getmBmap(), mSurvivor.getmX(),
                    mSurvivor.getmY(), mPaintBrush);
            mHolder.unlockCanvasAndPost(mBackground); // drawing done -> unlock background
        }
    }

    /** Controls updating of the thread. */
    private void framesPerSecond() {
        try{
            mGameThread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Pauses the game. */
    public void pause() {
        mIsPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {

        }
    }

    /** Resumes game after being paused. */
    public void resumeGame() {
        mIsPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent mEvent) {
        int actionIndex = mEvent.getActionIndex();
        int action = mEvent.getActionMasked();
        int ptrID = mEvent.getPointerId(actionIndex);
        int theX = (int) mEvent.getX(ptrID);
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                mSurvivor.move(theX);
                updateFrame();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }
}
