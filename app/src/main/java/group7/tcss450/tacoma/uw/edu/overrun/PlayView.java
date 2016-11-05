package group7.tcss450.tacoma.uw.edu.overrun;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import static android.R.attr.screenSize;

/** This class is intended for use in the game Overrun. A fun and fast-paced survival
 * game. This class holds the view for the in-app gameplay.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 1 Nov 2016
 */
public class PlayView extends SurfaceView implements Runnable{

    /** For discerning between tap and scroll. */
    public static final float TAP_TOLERANCE = 10;

    /** A volatile boolean for controlling multithreaded play. */
    private volatile boolean mIsPlaying; // True when game is in-play, false otherwise

    /** The thread for the playing the game. */
    private Thread mGameThread;

    /** The player's character of the game. */
    private Survivor mSurvivor;

    /** The paint object for use in drawing. */
    private Paint mPaintBrush;

    /** The canvas (background) object to draw on. */
    private Canvas mBackground;

    /** The holder for this surface view. */
    private SurfaceHolder mHolder;

    /** The player's weapon. */
    private Weapon mWeapon;

    /** The size of the screen being used to display the game. */
    private Point mScreen;

    /** Coordinates of the most recent touch on the screen. */
    private float mTouchX;
    private float mTouchY;

    /** Array for holding zombie objects. */
    private Zombie[] zombies;

    /** Adding 3 zombies for testing. */
    private int zombieCount = 3;

    /**
     * Constructor for the PlayView class.
     * @param context the context for the app.
     */
    public PlayView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        mScreen = new Point();
        d.getSize(mScreen);
        Log.d("ScreenX/2, ScreenY/3", "(" + mScreen.x/2 + "," +  mScreen.y/3 + ")");

        // create survivor object
        mSurvivor = new Survivor(context, mScreen);

        // create paint object for rendering
        mPaintBrush = new Paint();

        //create holder for view
        mHolder = getHolder();

        mWeapon = new Weapon(1, 1, mScreen, context);

        //zombies
        zombies = new Zombie[zombieCount];
        for (int i = 0; i < zombieCount; i++) {

            zombies[i] = new ZombieCrawler(context, mScreen);
        }
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
            update();
            draw();
            //update the frame controls
            framesPerSecond();
        }

    }

    /** Updates the frame for the PlayView. */
    private void update(){
        if(mWeapon.getmBullet().getmIsActive()) {
            mWeapon.getmBullet().updateBulletPosition();
        }
    }

    /** Draws the frame for the PlayView. */
    private void draw() {
        // draw all graphics
        // check holder
        if( mHolder.getSurface().isValid()) {
            mBackground = mHolder.lockCanvas(); // lock the background for drawing
            mBackground.drawColor(Color.BLACK); // color the background black
            mBackground.drawBitmap(mSurvivor.getmBmap(), mSurvivor.getmX(),
                    mSurvivor.getmY(), mPaintBrush);
            if(mWeapon.getmBullet().getmIsActive()) {
                mBackground.drawBitmap(mWeapon.getmBullet().getmBMP(), mWeapon.getmBullet().getmX(),
                        mWeapon.getmBullet().getmY(), mPaintBrush);
            }
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
    public void pauseGame() {
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
        switch(mEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = mEvent.getX();
                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                if(mEvent.getY() < mScreen.y - mScreen.y/3) {
                    mWeapon.shootWeapon(mSurvivor.getmX(), mSurvivor.getmY() -
                            mSurvivor.getmBmap().getHeight());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(mEvent.getY() >= mScreen.y - mScreen.y/3) {
                    if (TAP_TOLERANCE < Math.abs(mTouchX - mEvent.getX())) {
                        mSurvivor.setmX((int) mEvent.getX());
                    }
                }
                break;
        }
        return true;
    }
}
