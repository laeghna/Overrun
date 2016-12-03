package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * Bullet class for Overrun.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 02 December 2016
 */

public class Bullet extends BitmapResizer {

    public static final int AMMO_CAPACITY = 25;

    /** Bullet's speed. */
    private static final int SPEED = 15;

    /** Constant for scaling bullet. */
    private static final int SCALE = 25;

    /** The amt of damage the weapon does with each hit. */
    private int mDamage;

    /** The context for the bullet. */
    private Context mContext;

    /** The size of the screen. */
    private Point mScreenSize;

    /** Bitmap image for the bullets. */
    private Bitmap mBMP;

    /** The x- position of the bullet. */
    private int mX;

    /** The y - position of the bullet. */
    private int mY;

    /** Rectangle for bullet to determine collisions. */
    private Rect mDetectBullet;

    /** True if the bullet is still on the screen, false otherwise. */
    private boolean mIsActive;

    /**
     * Public constructor for the Bullet class.
     * @param dmg - the damage the weapon does per hit.
     * @param screenSize - the size of the screen.
     * @param context - the context for the app.
     */
    public Bullet(int dmg, Point screenSize, Context context) {
        mContext = context;
        mScreenSize = screenSize;
        mDamage = dmg;

        mBMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bullet); // a placeholder graphic
        mBMP = getResizedBmp(mBMP, screenSize.x/SCALE, screenSize.x/SCALE);

        mDetectBullet = new Rect();
        mIsActive = false;
    }

    /**
     * Gets the bitmap for the bullets.
     * @return - the bitmap for the bullets.
     */
    public Bitmap getBMP() {
        return mBMP;
    }

    /**
     * Gets the x position of the bullet.
     * @return - the x-coordinate of the bullet.
     */
    public int getX() {
        return mX;
    }

    /**
     * Sets the x-coordinate position of the bullet to the desired location.
     * @param mX - the new x-coordinate of the bullet.
     */
    public void setX(int mX) {
        this.mX = mX;
    }

    /**
     * Gets the y-coordinate position of the bullet.
     * @return - the y-coord position of the bullet.
     */
    public int getY() {
        return mY;
    }

    /**
     * Sets the y-coordinate of the position of the bullet to the desired location.
     * @param mY - the new y-coord position of the bullet.
     */
    public void setY(int mY) {
        this.mY = mY;
    }

    /**
     * Gets the collision detector for this bullet.
     * @return thw collision detector for this bullet.
     */
    public Rect getDetectBullet() {
        return mDetectBullet;
    }

    /**
     * Updates the position of the survivor's bullet.
     */
    public void updateBulletPosition() {
        if(mY - 1 > 0) {
            mY -= SPEED;

            //Adding the top, left, bottom and right to the rect object
            mDetectBullet.left = mX;
            mDetectBullet.top = mY - mBMP.getHeight();
            mDetectBullet.right = mX + mBMP.getWidth();
            mDetectBullet.bottom = mY;
        } else {
            mIsActive = false;
            resetBullet();
        }
    }

    /**
     * Gets the status of the bullet being drawn currently.
     * @return mIsActive - true if the bullet is active, false otherwise.
     */
    public boolean getIsActive() {return mIsActive;}

    /**
     * Shoots the weapon, adding a new bullet to recently fired from the specified position.
     * @param theX - the x coordinate of the bullet fired.
     * @param startY - the y coordinate of the bullet fired.
     */
    public void shootWeapon(int theX, int startY) {
        Log.d("shooting:", theX + " " + startY);
        if (!mIsActive) {
            mX = theX;
            mY = startY - mBMP.getHeight();
            mDetectBullet.left = mX;
            mDetectBullet.top = mY - mBMP.getHeight();
            mDetectBullet.right = mX + mBMP.getWidth();
            mDetectBullet.bottom = mY;
            mIsActive = true;
        }
    }

    /**
     * Resets bullet position.
     */
    public void resetBullet() {

        mX = 0;
        mY = mScreenSize.y;
        mDetectBullet.left = mX;
        mDetectBullet.top = mY - mBMP.getHeight();
        mDetectBullet.right = mX + mBMP.getWidth();
        mDetectBullet.bottom = mY;
        mIsActive = false;
    }
}
