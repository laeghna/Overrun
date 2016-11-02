package group7.tcss450.tacoma.uw.edu.overrun;

import android.graphics.Bitmap;

/**
 * This interface specifies the behavior for a zombie enemy.
 *
 * @author Lisa Taylor
 * @version 1 Nov 2016
 */

public interface Zombie {

    //Resize the image
    public Bitmap getResizedBmp(float newWidth, float newHeight);

    public void updateMovement();

    Bitmap getBitmap();

    int getXCoord();

    int getYCoord();

    int getSpeed();
}
