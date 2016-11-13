package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Interface contains one method signature:
 * getRisizedBmp for resizing bitmaps based on screen size.
 *
 * @author Leslie Pedro
 * @version 23 October 2016
 */

public abstract class GameCharacter {

    /**
     * Resizes the bitmap to the proper size for the screen in use.
     * @param newWidth - the new width for the bitmap.
     * @param newHeight - the new height for the bitmap.
     * @return the new, resized, bitmap.
     */
    public Bitmap getResizedBmp(Bitmap bmp, float newWidth, float newHeight) {
        int bm_w = bmp.getWidth();
        int bm_h = bmp.getHeight();
        float scale_w = newWidth / bm_w;
        float scale_h = newHeight / bm_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap resizedBMP = Bitmap.createBitmap(bmp, 0, 0, bm_w, bm_h, matrix, false);
        bmp.recycle();
        return resizedBMP;
    }
}
