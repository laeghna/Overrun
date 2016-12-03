package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

import group7.tcss450.tacoma.uw.edu.overrun.R;


/**
 * Intended for use with the game Overrun, a fun and fast-paced survival/shooter game.
 * This class creates the player, or the survivor, in the Overrun universe.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 22 November 2016
 */
public class Survivor extends GameCharacter{

    /** Constant for scaling survivor */
    private static final int SCALE = 15;

    /** The bitmap for the survivor sprite image. */
    private Bitmap mBmap;

    /** The position (coordinates) of the survivor on the view. */
    private int mX; // x- coordinate
    private int mY; // y- coordinate

    /** The move speed of the survivor. */
    private int mSpeed;

    /** Padding for the top and bottom of the game screen. */
    private int mPadBott = 165;

    /** Collision detector for the survivor. */
    private Rect mDetectCollisions;


    /**
     * Contructor to initialize variables.
     * @param context - the context for the application this game is played from
     */
    public Survivor(Context context, Point screenSize) {

        if(screenSize.x < 0 || screenSize.y < 0) {
            throw new IllegalArgumentException("cannot accept negative screen size.");
        }
        mSpeed = 2; // test speed may need to adjust

        // Get the player graphic from drawable:
        mBmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie); // a placeholder graphic
        mBmap = getResizedBmp(mBmap, screenSize.x/SCALE, screenSize.x/SCALE);
        mX = mBmap.getWidth();
        mY = screenSize.y - (mBmap.getHeight() + mPadBott);
        mDetectCollisions = new Rect(mX, mY, mX + mBmap.getWidth(), mY + mBmap.getHeight());
        //TODO: replace with correct graphics
    }

    /**
     * Gets the bitmap for the survivor image.
     * @return returns the image.
     */
    public Bitmap getmBmap() {
        return mBmap;
    }

    /**
     * Gets the x-coordinate of the survivor.
     * @return mX - the x coordinate of the survivor.
     */
    public int getmX() {
        return mX;
    }

    /**
     * Sets the position of the survivor to the new x location on the screen.
     * @param x - the new x position.
     */
    public void setmX(int x) {
        mX = x;
    }

    /**
     * Gets the y-coordinate of the survivor.
     * @return mY - the y coordinate of the survivor.
     */
    public int getmY() {
        return mY;
    }

    /**
     * Gets the survivor's move speed.
     * @return mSpeed - the survivor's move speed.
     */
    public int getmSpeed() {
        return mSpeed;
    }

    /** Gets the collision detector for the survivor. */
    public Rect getmDetectCollisions() {
        return mDetectCollisions;
    }

    /** Update the collision detector to the new position. */
    public void updateCollisionDetector() {
        mDetectCollisions.set(mX, mY, mX + mBmap.getWidth(), mY + mBmap.getHeight());
    }
}
