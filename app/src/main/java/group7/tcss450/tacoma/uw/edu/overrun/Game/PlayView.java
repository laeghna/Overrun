package group7.tcss450.tacoma.uw.edu.overrun.Game;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Random;

import group7.tcss450.tacoma.uw.edu.overrun.R;

/** This class is intended for use in the game Overrun. A fun and fast-paced survival
 * game. This class holds the view for the in-app gameplay.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 22 Nov 2016
 */
public class PlayView extends SurfaceView implements Runnable{

    /** The zombie count capacity for the level 1 difficulty. */
    private static final int COUNT_LEVEL_1 = 40;

    /** The zombie count capacity for the level 2 difficulty. */
    private static final int COUNT_LEVEL_2 = 65;

    /** The zombie count capacity for the level 3 difficulty. */
    private static final int COUNT_LEVEL_3 = 90;

    /** A volatile boolean for controlling multithreaded play. */
    private volatile boolean mIsPlaying; // True when game is in-play, false otherwise

    /** The thread for the playing the game. */
    private Thread mGameThread;

    /** The player's character of the game. */
    private Survivor mSurvivor;

    /** the survivor's health. */
    private Health health;

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

    /** The score for the game. */
    private int gameScore;

    private SharedPreferences mSharedPref;


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
        mSharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        // create survivor object
        mSurvivor = new Survivor(context, mScreen);

        // create health object
        health = new Health(context, mScreen);

        // create paint object for rendering
        mPaintBrush = new Paint();

        //create holder for view
        mHolder = getHolder();

        mBullets = new Bullet[Bullet.AMMO_CAPACITY];
        for(int i = 0; i < mBullets.length; i++) {

            mBullets[i] = new Bullet(1, mScreen, context);
        }

        int level = mSharedPref.getInt("saved_difficulty", 1);
        setupLevelDifficulty(level);

        //zombies
        zombies = new Zombie[zombieCount];
        //zombies[0] = new ZombieCrawler(context, mScreen);
        //zombies[0].setIsActive(true);
        Random random = new Random();
        int zombie = 0;
        for(int i = 0; i < zombies.length; i++) {

            zombie = random.nextInt(3);
            Log.d("RANDOM", "" + zombie);
            switch(zombie) {

                case 0: zombies[i] = new ZombieCrawler(context, mScreen);
                    break;
                case 1: zombies[i] = new ZombieWalker(context, mScreen);
                    break;
                case 2: zombies[i] = new ZombieColossus(context,mScreen);
                    break;
                default: zombies[i] = new ZombieCrawler(context, mScreen);
                    break;
            }

            zombies[i].setIsActive(true);
        }

        mBarrier = new Barrier(mScreen, mSurvivor);

        gameScore = 0;
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
        for(int i = 0; i < mBullets.length; i++) {
            if(mBullets[i] != null) {
                if (mBullets[i].getIsActive()) {
                    mBullets[i].updateBulletPosition();
                }
            }
        }

        // update zombie positions.
        for(int i=0; i < zombies.length; i++){

            //check if zombie is at barrier
            if(zombies[i] != null) {

                if(mBarrier.detectCollisions(zombies[i])) {
                    // do not update position
                } else if(zombies[i].getIsActive()) {
                    zombies[i].updateMovement();
                }
            }
        }

        //check for collisions between bullets and zombies.
        for(int i = 0; i < mBullets.length; i++) {
            if (mBullets[i] != null && mBullets[i].getIsActive()) {

                for (int z = 0; z < zombieCount; z++) {
                    //if collision occurs with bullet
                    if (Rect.intersects(mBullets[i].getDetectBullet(), zombies[z].getDetectZombie())) {
                        //Increase zombie's hit count and set bullet's isActive to false
                        zombies[z].addHit();
                        mBullets[i].resetBullet();
                        //If hit count is equal to zombie's health, reset zombie
                        if (zombies[z].getTimesHit() == zombies[z].getHP()) {
                            zombies[z].resetZombie();
                        }
                    }
                }
            }
        }

        //check for collisions between survivor and zombies
        //lower health if collision occurs
        for(int i = 0; i < zombieCount; i++) {
            if(zombies[i] != null && Rect.intersects(zombies[i].getDetectZombie(),
                    mSurvivor.getmDetectSurvivor())) {

                health.setCurrHealth(health.getCurrHealth() - 1);
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

            //Draw current health
            for(int h = 0; h < health.getCurrHealth(); h++) {

                mBackground.drawBitmap(health.getmBitmap(), health.getxCoord() +
                        (health.getmBitmap().getWidth() * h),
                        health.getyCoord(), mPaintBrush);
            }

            //Draw zombies
            for (int i = 0; i < zombies.length; i++) {

                if (zombies[i] != null && zombies[i].getIsActive()) {
                    mBackground.drawBitmap(
                            zombies[i].getBitmap(),
                            zombies[i].getXCoord(),
                            zombies[i].getYCoord(),
                            mPaintBrush);
                }
            }
            // draws bullets
            for(int i = 0; i < mBullets.length; i++) {
                if (mBullets[i] != null && mBullets[i].getIsActive()) {

                    mBackground.drawBitmap(mBullets[i].getBMP(), mBullets[i].getX(),
                            mBullets[i].getY(), mPaintBrush);
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
        mSurvivor.updateDetectSurvivor();
    }

    /**
     * Moves the survivor towards the right end of the screen.
     */
    public void moveRight() {
        if((mSurvivor.getmX() + mSurvivor.getmSpeed() + mSurvivor.getmBmap().getWidth()) < mScreen.x) {
            mSurvivor.setmX( mSurvivor.getmX() + mSurvivor.getmSpeed());
        }
        mSurvivor.updateDetectSurvivor();
    }

    /**
     * Fires the survivor's weapon.
     * @return true if the bullet was fired (added to the bullet array), false otherwise.
     */
    public boolean fire() {
        Log.d("firing", String.valueOf(mBarrier.getmStartY()));
        for(int i = 0; i < Bullet.AMMO_CAPACITY; i++) {
            if(!mBullets[i].getIsActive()) {
                mBullets[i].shootWeapon(mSurvivor.getmX() + (mSurvivor.getmBmap().getWidth() / 2),
                        mSurvivor.getmY());
                return true;
            }
        }
        return false;
    }

    /**
     * Method for initializing zombieCount based on level difficulty.
     */
    private void setupLevelDifficulty(int level) {

        switch(level) {
            case 1: zombieCount = COUNT_LEVEL_1;
                break;
            case 2: zombieCount = COUNT_LEVEL_2;
                break;
            case 3: zombieCount = COUNT_LEVEL_3;
                break;
            default: zombieCount = COUNT_LEVEL_1;
                break;
        }
    }
}


