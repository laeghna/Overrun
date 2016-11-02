package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;


/**
 * Intended for use with the game Overrun, a fun and fast-paced survival/shooter game.
 * This class creates the player, or the survivor, in the Overrun universe.
 */
public class Survivor implements GameCharacter{

    /** The bitmap for the survivor sprite image. */
    private Bitmap mBmap;

    /** The position (coordinates) of the survivor on the view. */
    private int mX; // x- coordinate
    private int mY; // y- coordinate

    /** The move speed of the survivor. */
    private int mSpeed;

    /** The collision detector for the survivor.*/
    private CollisionDetector mCollisionDetect;

    /**
     * Contructor for Survivor class.
     * @param context - the context for the application this game is played from
     */
    public Survivor(Context context, Point screenSize) {
        mSpeed = 5; // test speed may need to adjust
        //resize the bitmap
        float w_scale = ((float) screenSize.y) / 20; // Swap x and y due to forced landscape view
        float h_scale = ((float) screenSize.x) / 20;
        // Get the player graphic from drawable:
        Log.d("OVERRUN: SURVIVOR", "Screen: (" + screenSize.x + "," + screenSize.y + ")");
        mBmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie); // a placeholder graphic
        Log.d("OVERRUN: SURVIVOR", "Before Resize: (" +  mBmap.getWidth() +","+ mBmap.getHeight() + ")");
        mBmap = getResizedBmp(w_scale, h_scale);
        Log.d("OVERRUN: SURVIVOR", "After Resize: (" +  mBmap.getWidth() +","+ mBmap.getHeight() + ")");
        mX = mBmap.getWidth();
        mY = screenSize.y - mBmap.getHeight() * 3;
        mCollisionDetect = new CollisionDetector(mBmap.getHeight(), mBmap.getWidth(), mX, mY);
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
     * Gets the y-coordinate of the survivor.
     * @return mY - the y coordinate of the survivor.
     */
    public int getmY() {
        return mY;
    }

    /**
     * Gets the collision detector for this Survivor.
     * @return the collision detector.
     */
    public CollisionDetector getmCollisionDetect() {
        return mCollisionDetect;
    }

    /**
     * Gets the survivor's move speed.
     * @return mSpeed - the survivor's move speed.
     */
    public int getmSpeed() {
        return mSpeed;
    }

    public void move(int newX) {
        if(mX < newX) {
            moveRight(newX);
        } else if (mX > newX) {
            moveLeft(newX);
        }
        mCollisionDetect.setmPosition(new Point(mX, mY));
    }

    /**
     * Updates the survivor's position when the player moves right.
     */
    public void moveRight(int newX) {
        while(mX < newX) {
            mX += mSpeed;
        }
    }

    /**
     * Updates the survivor's position when the player moves left.
     */
    public void moveLeft(int newX) {
        while( mX > newX) {
            mX -= mSpeed;
        }
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
