package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * This interface specifies the behavior for a zombie enemy.
 *
 * @author Lisa Taylor
 * @version 30 Nov 2016
 */

public interface Zombie {

    /**
     * Gets the zombie's hit points.
     * @return the hit points
     */
    int getHP();

    /** Updates the image's coordinates.
     */
    void updateMovement();

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

    /**
     * Gets the status of the crawler being drawn currently.
     * @return isActive - true if the crawler is active, false otherwise.
     */
    boolean getIsActive();

    /**
     * Sets the status of the current crawler.
     * @param status true if the crawler is active, false otherwise.
     */
    void setIsActive(boolean status);

    /**
     * Gets the number of times the zombie has been hit.
     * @return timesHit the number of times a bullet hit the zombie
     */
    int getTimesHit();

    /**
     * Increments the hit count for when a bullet hits the zombie.
     */
    void addHit();

    /**
     * Gets the zombie's point value for adding to the game's score.
     * @return pointValue the zombie's point value
     */
    int getPointValue();

    /**
     * Resets zombie and rectangle start position.
     */
    public void resetZombie();
}
