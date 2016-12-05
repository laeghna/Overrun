package group7.tcss450.tacoma.uw.edu.overrun.Game;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

import group7.tcss450.tacoma.uw.edu.overrun.R;

/** This class is intended for use in the game Overrun. A fun and fast-paced survival
 * game. This class holds the view for the in-app gameplay.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 04 December 2016
 */
public class PlayView extends SurfaceView implements Runnable{

    /** The zombie count capacity for the level 1 difficulty. */
    private static final int COUNT_LEVEL_1 = 40;

    /** The zombie count capacity for the level 2 difficulty. */
    private static final int COUNT_LEVEL_2 = 65;

    /** The zombie count capacity for the level 3 difficulty. */
    private static final int COUNT_LEVEL_3 = 90;

    /** The hit delay for when a zombie and survivor collide. */
    private static final int HIT_DELAY = 100;

    /** A volatile boolean for controlling multithreaded play. */
    private volatile boolean mIsPlaying; // True when game is in-play, false otherwise

    /** The thread for the playing the game. */
    private Thread mGameThread;

    /** The player's character of the game. */
    private Survivor mSurvivor;

    /** the survivor's health. */
    private Health health;

    /** The paint object for use in drawing. */
    private Paint paint;

    /** The canvas (background) object to draw on. */
    private Canvas canvas;

    /** The holder for this surface view. */
    private SurfaceHolder mHolder;

    /** The player's weapon. */
    private Bullet[] mBullets;

    /** The size of the screen being used to display the game. */
    private Point mScreen;

    /** The game's Context. */
    private Context gameContext;

    /** Array for holding zombie objects. */
    private Zombie[] zombies;

    /** Adding 3 zombies for testing. */
    private int zombieCount = 3;

    /** The barrier between the survivor and the zombies. */
    private Barrier mBarrier;

    /** The score for the game. */
    private int gameScore;

    /** The current level setting. */
    private int level;

    /** The hit delay for when a zombie and survivor collide. */
    private int hitDelayCounter = 0;

    /** Bitmap for the background image. */
    Bitmap bgImage;

    /** Shared Preferences for this game. */
    private SharedPreferences mSharedPref;

    /** Boolean indicating game state. True if game is over, false otherwise. */
    private volatile boolean isGameOver;

    /** The property change support for this class. */
    private PropertyChangeSupport changeSupport;

    /**
     * Constructor for the PlayView class.
     * @param context the context for the app.
     */
    public PlayView(Context context) {
        super(context);
        changeSupport = new PropertyChangeSupport(this);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        mScreen = new Point();
        isGameOver = false;
        gameContext = context;
        d.getSize(mScreen);
        bgImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.dirty_road);
        bgImage = Bitmap.createScaledBitmap(bgImage, mScreen.x, mScreen.y, false);
        mSharedPref = context.getSharedPreferences(
                context.getString(R.string.shared_prefs), Context.MODE_PRIVATE);

        // create survivor object
        mSurvivor = new Survivor(context, mScreen);

        // create health object
        health = new Health(context, mScreen);

        // create paint object for rendering
        paint = new Paint();

        //create holder for view
        mHolder = getHolder();

        mBullets = new Bullet[Bullet.AMMO_CAPACITY];
        for(int i = 0; i < mBullets.length; i++) {

            mBullets[i] = new Bullet(1, mScreen, context);
        }

        level = mSharedPref.getInt("saved_difficulty", 1);
        setupLevelDifficulty(level);

        //zombies
        zombies = new Zombie[zombieCount];
        spawnZombie();

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
            while (mIsPlaying) {

                //update and draw frame
                update();
                draw();
                //update the frame controls
                framesPerSecond();
            }
    }

    /** Updates the frame for the PlayView. */
    private void update() {

        // update zombie positions.
        for (int i = 0; i < zombies.length; i++) {


            if (zombies[i] != null) {

                if (mBarrier.detectCollisions(zombies[i])) {   //check if zombie is at barrier or survivor
                    // do not update position
                } else if (Rect.intersects(zombies[i].getDetectZombie(),  //collision between survivor and zombie
                        mSurvivor.getmDetectSurvivor())) {

                    if (hitDelayCounter == 0) {

                        health.setCurrHealth(health.getCurrHealth() - 1);
                        Log.d("PlayView", "Survivor is hit!");

                    } else if (hitDelayCounter == HIT_DELAY)
                        hitDelayCounter = -1;

                    hitDelayCounter++;

                } else if (zombies[i].getIsActive()) {

                    zombies[i].updateMovement();
                }
            }
            if(health.getCurrHealth() <= 0) {
                isGameOver = true;
            }
        }

        //update bullet positions
        //check for collisions between bullets and zombies.
        for (int i = 0; i < mBullets.length; i++) {
            if (mBullets[i] != null && mBullets[i].getIsActive()) {

                mBullets[i].updateBulletPosition();

                for (int z = 0; z < zombieCount; z++) {
                    //if collision occurs with bullet
                    if (zombies[z] != null && Rect.intersects(mBullets[i].getDetectBullet(),
                            zombies[z].getDetectZombie())) {
                        //Increase zombie's hit count and reset bullet
                        zombies[z].addHit();
                        mBullets[i].resetBullet();
                        //If hit count is equal to zombie's health, reset zombie and increase score
                        if (zombies[z].getTimesHit() == zombies[z].getHP()) {
                            zombies[z].resetZombie();
                            gameScore = gameScore + zombies[z].getPointValue();
                            Log.d("SCORE", "" + gameScore);
                        }
                    }
                }
            }
        }
    }

    /** Draws the frame for the PlayView. */
    private void draw() {

        // draw all graphics
        // check holder
        if( mHolder.getSurface().isValid()) {
            canvas = mHolder.lockCanvas(); // lock the background for drawing
            canvas.drawBitmap(bgImage, 0, 80, paint);
            canvas.drawBitmap(mSurvivor.getmBmap(), mSurvivor.getmX(),
                    mSurvivor.getmY(), paint);

            mBarrier.drawBarrier(paint, canvas);

            //Draw zombies
            for (int i = 0; i < zombies.length; i++) {

                if (zombies[i] != null && zombies[i].getIsActive()) {
                    canvas.drawBitmap(
                            zombies[i].getBitmap(),
                            zombies[i].getXCoord(),
                            zombies[i].getYCoord(),
                            paint);
                }
            }
            // draws bullets
            for(int i = 0; i < mBullets.length; i++) {
                if (mBullets[i] != null && mBullets[i].getIsActive()) {

                    canvas.drawBitmap(mBullets[i].getBMP(), mBullets[i].getX(),
                            mBullets[i].getY(), paint);
                }
            }

            paint.setColor(Color.BLACK);
            canvas.drawRect(0, 0, mScreen.x, 80, paint);


            //Draw current health
            for(int h = 0; h < health.getCurrHealth(); h++) {

                canvas.drawBitmap(health.getmBitmap(), health.getxCoord() +
                                (health.getmBitmap().getWidth() * h),
                        health.getyCoord(), paint);
            }

            //Draw updated score
            paint.setTextSize(60);
            paint.setColor(Color.GREEN);
            canvas.drawText("Score: " + gameScore, mScreen.x - 500, 60, paint);

            if(isGameOver) {
                paint.setTextSize(150);
                canvas.drawText("GAME OVER", mScreen.x/2 - 50, mScreen.y/2, paint);
                mIsPlaying = false;
                changeSupport.firePropertyChange("GameOver", false, true);
            }
            mHolder.unlockCanvasAndPost(canvas); // drawing done -> unlock background
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

        for(int i = 0; i < Bullet.AMMO_CAPACITY; i++) {

            if(!mBullets[i].getIsActive()) {

                mBullets[i].shootWeapon(mSurvivor.getmX() + (mSurvivor.getmBmap().getWidth()),
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

    /**
     * Method for zombie spawning.
     */
    public boolean spawnZombie() {

        Random random = new Random();
        int zombie;

        for(int i = 0; i < zombies.length; i++) {

            if(zombies[i] == null || !zombies[i].getIsActive()) {

                zombie = random.nextInt(3);
                switch(zombie) {

                    case 0: zombies[i] = new ZombieCrawler(gameContext, mScreen);
                        break;
                    case 1: zombies[i] = new ZombieWalker(gameContext, mScreen);
                        break;
                    case 2: zombies[i] = new ZombieColossus(gameContext,mScreen);
                        break;
                    default: zombies[i] = new ZombieCrawler(gameContext, mScreen);
                        break;
                }

                zombies[i].setIsActive(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a property change listener to this observer.
     * @param listener the listener for this observer.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {

         changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Get the game's current level.
     * @return level the game level
     */
    public int getLevel() {
        return level;
    }

    public boolean getIsGameOver() {
        return isGameOver;
    }

    /**
     * Removes all callbacks.
     */
    public void endGame() {
        removeCallbacks(this);
    }
}