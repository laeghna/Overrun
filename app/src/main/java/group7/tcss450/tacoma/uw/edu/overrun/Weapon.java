package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * Weapon class for Overrun.
 *
 * @author Leslie Pedro
 * @author Lisa Taylor
 * @version 8 November 2016
 */

public class Weapon {

    /** The amt of damage the weapon does with each hit. */
    private int mDamage;

    /** The context for the bullet. */
    private Context mContext;

    /** The size of the screen. */
    private Point mScreenSize;

    /** The weapon's bullet. */
    private Bullet mBullet;

    /**
     * Public constructor for the Weapon class.
     * @param dmg - the damage the weapon does per hit.
     * @param capacity - the number of bullets.
     * @param screenSize - the size of the screen.
     * @param context - the context for the app.
     */
    public Weapon(int dmg, int capacity, Point screenSize, Context context) {
        mContext = context;
        mScreenSize = screenSize;
        mDamage = dmg;
        mBullet = new Bullet();
    }

    /**
     * Shoots the weapon, adding a new bullet to recently fired from the specified position.
     * @param theX - the x coordinate of the bullet fired.
     * @param startY - the y coordinate of the bullet fired.
     */
    public void shootWeapon(int theX, int startY) {
        if(!mBullet.getIsActive()) {
            mBullet.setX(theX);
            mBullet.setY(startY);
            mBullet.setIsActive(true);
        }
    }


    /** Get bullet object. */
    public Bullet getBullet() {return mBullet;}

    /** Get bullet Rect for detecting collisions. */
    public Rect getDetectBullet() {
        return mBullet.getDetectBullet();
    }

    /**
     * Inner class for weapon containing the properties of the weapon's ammo.
     */
    protected class Bullet implements GameCharacter{

        /** Bitmap image for the bullets. */
        private Bitmap mBMP;

        /** The x- position of the bullet. */
        private int mX;

        /** The y - position of the bullet. */
        private int mY;

        /** Rectangle for bullet to determine collisions. */
        private Rect mDetectBullet;

        private boolean mIsActive;


        /**
         * Constructor to initialize variables.
         */
        public Bullet() {
            mBMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bullets); // a placeholder graphic
            mIsActive = false;

            mDetectBullet =  new Rect(mX, mY, mBMP.getWidth(), mBMP.getHeight());
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
         * Resizes the bullet bitmap as needed. **** NOT CURRENTLY REQUIRED *****
         * @param newWidth - the new width of the bitmap.
         * @param newHeight - the new height of the bitmap.
         * @return the new bitmap.
         */
        @Override
        public Bitmap getResizedBmp(float newWidth, float newHeight) {
            int bm_w = mBMP.getWidth();
            int bm_h = mBMP.getHeight();
            float scale_w = newWidth / bm_w;
            float scale_h = newHeight / bm_h;
            Matrix matrix = new Matrix();
            matrix.postScale(scale_w, scale_h);
            Bitmap resizedBMP = Bitmap.createBitmap(mBMP, 0, 0, bm_w, bm_h, matrix, false);
            mBMP.recycle();
            return resizedBMP;
        }

        /**
         * Updates the position of the survivor's bullet.
         */
        public void updateBulletPosition() {
            if(mY - 1 > 0) {
                mY-=15;
            } else {
                mIsActive = false;
            }

            //Adding the top, left, bottom and right to the rect object
            mDetectBullet.left = mX;
            mDetectBullet.top = mY;
            mDetectBullet.right = mX + mBMP.getWidth();
            mDetectBullet.bottom = mY + mBMP.getHeight();
        }

        /**
         * Gets the status of the bullet being drawn currently.
         * @return mIsActive - true if the bullet is active, false otherwise.
         */
        public boolean getIsActive() {return mIsActive;}

        /**
         * Sets the status of the current bullet.
         * @param status true if the bullet is active, false otherwise.
         */
        public void setIsActive(boolean status) { mIsActive = status;}
    }
}
