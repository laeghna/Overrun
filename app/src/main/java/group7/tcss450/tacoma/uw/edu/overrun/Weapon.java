package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Weapon class for Overrun.
 * Created by lesliepedro on 10/29/16.
 */

public class Weapon {

    /** The amt of damage the weapon does with each hit. */
    private int mDamage;

    /** The amt of bullets in the weapon. */
    private int mAmmo;

    /** The number of bullets released with each shot fired. */
    private int mBursts;

    /** The context for the bullet. */
    private Context mContext;

    /** The size of the screen. */
    private Point mScreenSize;

    /** The shots recently fired. */
    private ArrayList<Bullet> mShotsFired;

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
        mShotsFired = new ArrayList<>();
        mShotsFired.add(new Bullet(150, 500));
        mShotsFired.add(new Bullet(250, 500));
        mDamage = dmg;
        mAmmo = capacity;
        mBursts = 1;
    }

    /**
     * Gets the damage per hit.
     * @return the damage the weapon does per hit.
     */
    public int getmDamage() {
        return mDamage;
    }

    /**
     * Gets the amount of ammo in the weapon.
     * @return the amount of ammo in the weapon.
     */
    public int getCapacity() {
        return mAmmo;
    }

    /**
     * Shoots the weapon, adding a new bullet to recently fired from the specified position.
     * @param theX - the x coordinate of the bullet fired.
     * @param startY - the y coordinate of the bullet fired.
     */
    public void shootWeapon(int theX, int startY) {
        for(int i = 0; i < mBursts; i++) {
            mShotsFired.add(new Bullet(theX, startY));
        }

    }

    /**
     * Updates the position for all bullets recently fired (aka moves them up the screen).
     */
    public void updateBulletPositions() {
        if(!mShotsFired.isEmpty()) {
            for (Bullet b : mShotsFired) {
                int currY = b.getmY();
                if (currY > 0) {
                    b.setmY(currY -= 1);

                } else if (currY <= 0) {
                    mShotsFired.remove(mShotsFired.indexOf(b));
                }
            }
        }
    }

    /**
     * Gets the list of bullets recently fired.
     * @return - the list of recently fired bullets.
     */
    public ArrayList<Bullet> getmShotsFired() {
        return mShotsFired;
    }


    /**
     * Inner class for weapon containing the properties of the weapon's ammo.
     */
    public class Bullet implements GameCharacter{

        /** Bitmap image for the bullets. */
        private Bitmap mBMP;

        /** The x- position of the bullet. */
        private int mX;

        /** The y - position of the bullet. */
        private int mY;

        /** CollisionDetector for the bullet. */
        private CollisionDetector mDetector;


        /**
         * Public Bullet constructor.
         * @param theX - the x position of the bullet.
         * @param startY - the y position for the bullet to start at.
         */
        public Bullet(int theX, int startY) {
            float w_scale = ((float) mScreenSize.y) / 40; // Swap x and y due to forced landscape view
            float h_scale = ((float) mScreenSize.x) / 40;
            mBMP = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bullets); // a placeholder graphic
//            mBMP = getResizedBmp(w_scale, h_scale); //TODO: update graphics
            mX = theX;
            mY = startY;
            mDetector = new CollisionDetector(mBMP.getHeight(), mBMP.getWidth(), mX, mY);
        }

        /**
         * Gets the bitmap for the bullets.
         * @return - the bitmap for the bullets.
         */
        public Bitmap getmBMP() {
            return mBMP;
        }

        /**
         * Gets the x position of the bullet.
         * @return - the x-coordinate of the bullet.
         */
        public int getmX() {
            return mX;
        }

        /**
         * Sets the x-coordinate position of the bullet to the desired location.
         * @param mX - the new x-coordinate of the bullet.
         */
        public void setmX(int mX) {
            this.mX = mX;
            mDetector.setmPosition(new Point(mX, mY));
        }

        /**
         * Gets the y-coordinate position of the bullet.
         * @return - the y-coord position of the bullet.
         */
        public int getmY() {
            return mY;
        }

        /**
         * Sets the y-coordinate of the position of the bullet to the desired location.
         * @param mY - the new y-coord position of the bullet.
         */
        public void setmY(int mY) {
            this.mY = mY;
            mDetector.setmPosition(new Point(mX, mY));
        }

        /**
         * Gets the collision detector for this bullet.
         * @return thw collision detector for this bullet.
         */
        public CollisionDetector getmDetector() {
            return mDetector;
        }

        /**
         * Sets the collision detector to a new collision detector object.
         * @param mDetector - the new collision detector.
         */
        public void setmDetector(CollisionDetector mDetector) {
            this.mDetector = mDetector;
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
    }
}
