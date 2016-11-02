package group7.tcss450.tacoma.uw.edu.overrun;

import android.graphics.Point;
import android.graphics.Rect;

/**
 * Detects collisions for game objects.
 *
 * Created by lesliepedro on 10/30/16.
 */

public class CollisionDetector {

    /** The dimensions for the collision detector (size of object).*/
    private Rect mDimensions;

    /** The position of the collision detector (usually same as object). */
    private Point mPosition;

    /**
     * Public constructor for CollisioDetector.
     * @param height - the height of the detector.
     * @param width - the width of the detector.
     * @param theX - the x-coordinate of the top left corner of the detector.
     * @param theY - the y-coordinate of the top left corner of the detector.
     */
    public CollisionDetector(int height, int width, int theX, int theY) {
        mDimensions = new Rect(theX, theY, width, height);
        mPosition = new Point(theX, theY);
    }

    /**
     * Checks an object to see if it has collided with another object.
     * @param object - the object that may have collided with this one.
     * @return true if a collision occured, false otherwise.
     */
    public boolean isCollision(CollisionDetector object) {
        boolean isHit = false;
        if(Rect.intersects(mDimensions, object.getmDimensions())) {
            isHit = true;
        }
        return isHit;
    }

    /**
     * Gets the dimensions of the detector.
     * @return the dimensions of the detector (a Rect object).
     */
    public Rect getmDimensions() {
        return mDimensions;
    }

    /**
     * Sets the dimensions of the detector.
     * @param mDimensions - the new dimensions for the detector.
     */
    public void setmDimensions(Rect mDimensions) {
        this.mDimensions = mDimensions;
    }

    /**
     * Gets the position of the detector.
     * @return the point containing the x and y coordinates of the current detector position.
     */
    public Point getmPosition() {
        return mPosition;
    }

    /**
     * Sets the position of the detector such that the top left corner of the detector
     * is at the point supplied.
     * @param mPosition - the point at which the top left corner of the detector shall reside.
     */
    public void setmPosition(Point mPosition) {
        this.mPosition = mPosition;
    }
}
