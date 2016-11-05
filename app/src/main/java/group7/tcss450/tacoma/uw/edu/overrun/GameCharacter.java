package group7.tcss450.tacoma.uw.edu.overrun;

import android.graphics.Bitmap;

/**
 * Interface for Overrun GameCharacters contains one method signature:
 * getRisizedBmp for resizing bitmaps based on screen size.
 *
 * @author Leslie Pedro
 * @version 23 October 2016
 */

public interface GameCharacter {

    public Bitmap getResizedBmp(float newWidth, float newHeight);
}
