package group7.tcss450.tacoma.uw.edu.overrun.Game;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/** This class is intended for use in the game Overrun. A fun and fast-paced survival
 * game. This class holds the view for the in-app gameplay.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 8 Nov 2016
 */
public class PlayView extends SurfaceView implements Runnable{

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
    private Bullet[] mBullets;

    /** The size of the screen being used to display the game. */
    private Point mScreen;

    /** Array for holding zombie objects. */
    private Zombie[] zombies;

    /** Adding 3 zombies for testing. */
    private int zombieCount = 3;

    /** The barrier between the survivor and the zombies. */
    private Barrier mBarrier;


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

        // create survivor object
        mSurvivor = new Survivor(context, mScreen);

        // create paint object for rendering
        mPaintBrush = new Paint();

        //create holder for view
        mHolder = getHolder();

        mBullets = new Bullet[Bullet.AMMO_CAPACITY];

        //zombies
        zombies = new Zombie[zombieCount];
        for (int i = 0; i < zombieCount; i++) {

            zombies[i] = new ZombieCrawler(context, mScreen);
        }

        mBarrier = new Barrier(mScreen, mSurvivor);
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
        // update active bullet positions.
        for(int i = 0; i < Bullet.AMMO_CAPACITY; i++) {
            if(mBullets[i] != null) {
                if (mBullets[i].getIsActive()) {
                    mBullets[i].updateBulletPosition();
                }
            }
        }

        // update zombie positions.
        for(int i=0; i < zombies.length; i++){

            if(mBarrier.detectCollisions(zombies[i])) {
                // do not update position
            } else {
                zombies[i].updateMovement();
            }
        }

        //check for collisions between bullets and zombies.
        for(int i = 0; i < Bullet.AMMO_CAPACITY; i++) {
            if (mBullets[i] != null) {
                if (mBullets[i].getIsActive()) {
                    for (int z = 0; z < zombieCount; z++) {
                        //if collision occurs with bullet
                        if (Rect.intersects(mBullets[i].getDetectBullet(), zombies[z].getDetectZombie())) {
                            //moving enemy outside the bottom edge and setting bullet isActive to false
                            zombies[z].setXCoord(mScreen.y + zombies[z].getBitmap().getHeight());
                            mBullets[i].setIsActive(false);
                        }
                    }
                }
            }
        }
        for(int i = 0; i < zombieCount; i++) {
            if(Rect.intersects(zombies[i].getDetectZombie(), mSurvivor.getmDetectCollisions())) {
                Log.d("PlayView", "Survivor is hit!");
            }
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

            mBarrier.drawBarrier(mPaintBrush, mBackground);

            //Draw zombies
            for (int i = 0; i < zombies.length; i++) {
                mBackground.drawBitmap(
                        zombies[i].getBitmap(),
                        zombies[i].getXCoord(),
                        zombies[i].getYCoord(),
                        mPaintBrush
                );
            }
            // draws bullets
            for(int i = 0; i < Bullet.AMMO_CAPACITY; i++) {
                if (mBullets[i] != null) {
                    if (mBullets[i].getIsActive()) {
                        mBackground.drawBitmap(mBullets[i].getBMP(), mBullets[i].getX(),
                                mBullets[i].getY(), mPaintBrush);
                    }
                }
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

    /**
     * Moves the survivor toward the left end of the screen.
     */
    public void moveLeft() {
        if((mSurvivor.getmX() - mSurvivor.getmSpeed()) > 1) {
            mSurvivor.setmX(mSurvivor.getmX() - mSurvivor.getmSpeed());
        }
        mSurvivor.updateCollisionDetector();
    }

    /**
     * Moves the survivor towards the right end of the screen.
     */
    public void moveRight() {
        if((mSurvivor.getmX() + mSurvivor.getmSpeed() + mSurvivor.getmBmap().getWidth()) < mScreen.x) {
            mSurvivor.setmX( mSurvivor.getmX() + mSurvivor.getmSpeed());
        }
        mSurvivor.updateCollisionDetector();
    }

    /**
     * Fires the survivor's weapon.
     * @return true if the bullet was fired (added to the bullet array), false otherwise.
     */
    public boolean fire() {
        Bullet b = new Bullet(1, mScreen, getContext());
        b.shootWeapon(mSurvivor.getmX(), mSurvivor.getmY());
        for(int i = 0; i < Bullet.AMMO_CAPACITY; i++) {
            if(mBullets[i] == null || !mBullets[i].getIsActive()) {
                mBullets[i] = b;
                return true;
            }
        }
        return false;
    }
}


