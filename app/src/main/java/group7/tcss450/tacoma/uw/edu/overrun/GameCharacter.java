package group7.tcss450.tacoma.uw.edu.overrun;

import android.graphics.Bitmap;

/**
 * Interface for Overrun GameCharacters contains one method signature:
 * getRisizedBmp for resizing bitmaps based on screen size.
 * Created by lesliepedro on 10/23/16.
 */

public interface GameCharacter {

    public Bitmap getResizedBmp(float newWidth, float newHeight);
}
