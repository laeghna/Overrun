package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * This interface specifies the behavior for a zombie enemy.
 *
 * @author Lisa Taylor
 * @version 8 Nov 2016
 */

public interface Zombie {

    /**
     * Gets resized bitmap image.
     * @param newWidth the width of the resized bitmap
     * @param newHeight the height of the resized bitmap
     * @return the resized bitmap
     */
    public Bitmap getResizedBmp(float newWidth, float newHeight);

    /** Updates the image's coordinates.
     */
    public void updateMovement();

    /**
     * Gets the zombie bitmap image.
     * @return the zombie bitmap image
     */
    Bitmap getBitmap();

    /**
     * Gets the zombie x coordinate.
     * @return the x coordinate
     */
    int getXCoord();

    /**
     * Sets the zombie x coordinate.
     * @param x the new x coordinate
     */
    void setXCoord(int x);

    /**
     * Gets the zombie y coordinate.
     * @return the y coordinate
     */
    int getYCoord();

    /**
     * Gets the zombie's speed.
     * @return the speed
     */
    int getSpeed();

    /**
     * Gets the zombie rectangle.
     * @return the zombie rectangle
     */
    Rect getDetectZombie();

    boolean getIsActive();

    void setIsActive(boolean b);
}
