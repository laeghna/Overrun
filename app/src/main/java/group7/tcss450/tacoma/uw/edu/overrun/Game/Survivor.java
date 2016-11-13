package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

import group7.tcss450.tacoma.uw.edu.overrun.R;


/**
 * Intended for use with the game Overrun, a fun and fast-paced survival/shooter game.
 * This class creates the player, or the survivor, in the Overrun universe.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 8 November 2016
 */
public class Survivor implements GameCharacter{

    /** Constant for scaling survivor */
    private static final int SCALE = 15;

    /** The bitmap for the survivor sprite image. */
    private Bitmap mBmap;

    /** The position (coordinates) of the survivor on the view. */
    private int mX; // x- coordinate
    private int mY; // y- coordinate

    /** The move speed of the survivor. */
    private int mSpeed;

    /** Boolean for determining if the game is running. */
    private boolean mIsRunning;

    /** Point value for the screen size. */
    private Point mScreen;

    /** Padding for the top and bottom of the game screen. */
    private int mPadBott = 175;
    private int mPadTop = 5;

    /**
     * Contructor to initialize variables.
     * @param context - the context for the application this game is played from
     */
    public Survivor(Context context, Point screenSize) {
        mScreen = screenSize;
        mSpeed = 2; // test speed may need to adjust
        //resize the bitmap
        float w_scale = ((float) screenSize.y) / SCALE; // Swap x and y due to forced landscape view
        float h_scale = ((float) screenSize.x) / SCALE;
        // Get the player graphic from drawable:
        mBmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie); // a placeholder graphic
        mBmap = getResizedBmp(w_scale, h_scale);
        mX = mBmap.getWidth();
        mY = screenSize.y - (mBmap.getHeight() + mPadBott);
        mIsRunning = false;
        //TODO: replace with correct graphics, fix bullets, and implement collision detection
    }

    /**
     * Gets mIsRunning.
     * @return true if game is running, else false
     */
    public boolean getmIsRunning() {
        return mIsRunning;
    }

    /**
     * Sets mIsRunning
     * @param mIsRunning new boolean value
     */
    public void setmIsRunning(boolean mIsRunning) {
        this.mIsRunning = mIsRunning;
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


    /**
     * Resizes the bitmap for the Survivor to the proper size for the screen in use.
     * @param newWidth - the new width for the bitmap.
     * @param newHeight - the new height for the bitmap.
     * @return the new, resized, bitmap.
     */
    public Bitmap getResizedBmp(float newWidth, float newHeight) {
        int bm_w = mBmap.getWidth();
        int bm_h = mBmap.getHeight();
        float scale_w = newWidth / bm_w;
        float scale_h = newHeight / bm_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap resizedBMP = Bitmap.createBitmap(mBmap, 0, 0, bm_w, bm_h, matrix, false);
        mBmap.recycle();
        return resizedBMP;
    }
}
