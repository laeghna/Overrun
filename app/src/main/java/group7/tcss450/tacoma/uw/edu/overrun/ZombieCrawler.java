package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

import java.util.Random;

/**
 * This class specifies the behavior for a zombie crawler enemy.
 * This is the slowest enemy.
 *
 * @author Lisa Taylor
 * @version 6 Nov 2016
 */

public class ZombieCrawler implements Zombie {

    /** Zombie crawler's speed */
    private static final int SPEED = 1;

    /** Constant for scaling zombie crawler */
    private static final int SCALE = 15;

    /** Zombie crawler image */
    private Bitmap crawlerBitmap;

    /** Zombie crawler coordinates */
    private int xCoord;
    private int yCoord;

    /** Screen coordinate sto ensure enemy stays within screen */
    private int xMin;
    private int xMax;

    private int yMin;
    private int yMax;

    public ZombieCrawler(Context context, Point screenSize) {

        crawlerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);

        xMin = 0;
        xMax = screenSize.x;
        yMin = 0;
        yMax = screenSize.y;

        Random genRandom = new Random();

        //resize the bitmap
        float w_scale = ((float) screenSize.y) / SCALE; // Swap x and y due to forced landscape view
        float h_scale = ((float) screenSize.x) / SCALE;

        // Get the zombie graphic from drawable:
        Log.d("OVERRUN: SURVIVOR", "Screen: (" + screenSize.x + "," + screenSize.y + ")");
        crawlerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie); // a placeholder graphic
        Log.d("OVERRUN: SURVIVOR", "Before Resize: (" +  crawlerBitmap.getWidth() +","+ crawlerBitmap.getHeight() + ")");
        crawlerBitmap = getResizedBmp(w_scale, h_scale);
        Log.d("OVERRUN: SURVIVOR", "After Resize: (" +  crawlerBitmap.getWidth() +","+ crawlerBitmap.getHeight() + ")");

        xCoord = genRandom.nextInt(xMax - crawlerBitmap.getWidth());
        yCoord = yMin;
    }

    @Override
    public Bitmap getResizedBmp(float newWidth, float newHeight) {
        int bmWidth = crawlerBitmap.getWidth();
        int bmHeight = crawlerBitmap.getHeight();
        float wScale = newWidth / bmWidth;
        float hScale = newHeight / bmHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(wScale, hScale);
        Bitmap resizedBMP = Bitmap.createBitmap(crawlerBitmap, 0, 0, bmWidth, bmHeight, matrix, false);
        crawlerBitmap.recycle();
        return resizedBMP;
    }

    @Override
    public void updateMovement() {

        yCoord += SPEED;

        //do something if enemy reaches bottom edge
    }

    @Override
    public Bitmap getBitmap() {
        return crawlerBitmap;
    }

    @Override
    public int getXCoord() {
        return xCoord;
    }

    @Override
    public int getYCoord() {
        return yCoord;
    }

    @Override
    public int getSpeed() {
        return SPEED;
    }

    public String toString() {
        return "crawler";
    }
}
