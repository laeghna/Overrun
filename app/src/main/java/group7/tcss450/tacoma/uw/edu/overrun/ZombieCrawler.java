package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
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

    /** Zombie crawler's speed. */
    private static final int SPEED = 1;

    /** Constant for scaling zombie crawler. */
    private static final int SCALE = 15;

    /** Zombie crawler image. */
    private Bitmap crawlerBitmap;

    /** Zombie crawler coordinates. */
    private int xCoord;
    private int yCoord;

    /** Screen coordinates to ensure enemy stays within screen. */
    private int xMin;
    private int xMax;

    private int yMin;
    private int yMax;

    /** A random generator for placing new crawlers. */
    private Random genRandom;

    /** Rectangle for crawler to determine collisions. */
    private Rect detectZombie;

    /** Boolean to determine if crawler should be drawn or not. */
    private boolean isActive;

    /** Constructor to initialize variables. */
    public ZombieCrawler(Context context, Point screenSize) {

        crawlerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);

        xMin = 0;
        xMax = screenSize.x;
        yMin = 0;
        yMax = screenSize.y;

        genRandom = new Random();

        //resize the bitmap, swap x and y due to force landscape view
        float wScale = ((float) screenSize.y) / SCALE;
        float hScale = ((float) screenSize.x) / SCALE;

        // Get the zombie graphic from drawable:
        Log.d("OVERRUN: SURVIVOR", "Screen: (" + screenSize.x + "," + screenSize.y + ")");

        // a placeholder graphic
        crawlerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);
        Log.d("OVERRUN: SURVIVOR", "Before Resize: (" +  crawlerBitmap.getWidth() +","+ crawlerBitmap.getHeight() + ")");

        crawlerBitmap = getResizedBmp(wScale, hScale);
        Log.d("OVERRUN: SURVIVOR", "After Resize: (" +  crawlerBitmap.getWidth() +","+ crawlerBitmap.getHeight() + ")");

        xCoord = genRandom.nextInt(xMax - crawlerBitmap.getWidth());
        yCoord = yMin;

        detectZombie =  new Rect(xCoord, yCoord, crawlerBitmap.getWidth(), crawlerBitmap.getHeight());

        isActive = false;
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

        if (yCoord + 1 < yMax) {
            yCoord += SPEED;
        } else {
            xCoord = genRandom.nextInt(xMax - crawlerBitmap.getWidth());
            yCoord = yMin;
            setIsActive(true);
        }

        //adding top, left, bottom and right to the rect object
        detectZombie.left = xCoord;
        detectZombie.top = yCoord;
        detectZombie.right = xCoord + crawlerBitmap.getWidth();
        detectZombie.bottom = yCoord + crawlerBitmap.getHeight();

        //do something if enemy reaches bottom edge
        //such as creating new zombie and reducing survivor health
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
    public void setXCoord(int x) {
        xCoord = x;
    }

    @Override
    public int getYCoord() {
        return yCoord;
    }

    @Override
    public int getSpeed() {
        return SPEED;
    }

    @Override
    public Rect getDetectZombie() {
        return detectZombie;
    }

    /**
     * Gets the status of the crawler being drawn currently.
     * @return isActive - true if the crawler is active, false otherwise.
     */
    public boolean getIsActive() {return isActive;}

    /**
     * Sets the status of the current crawler.
     * @param status true if the crawler is active, false otherwise.
     */
    public void setIsActive(boolean status) { isActive = status;}
}
