package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;

import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * This class specifies the behavior for a zombie colossus enemy.
 * This is the toughest enemy.
 *
 * @author Lisa Taylor
 * @version 22 Nov 2016
 */

public class ZombieColossus implements Zombie {

    /** Zombie's hit points - the shots needed to destroy zombie. */
    private static final int HP = 3;

    /** Zombie crawler's speed. */
    private static final int SPEED = 3;

    /** Constant for scaling zombie colossus. */
    private static final int SCALE = 15;

    /** Zombie crawler image. */
    private Bitmap colossusBitmap;

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

    /** The number of times the zombie has been hit by a bullet. */
    private int timesHit = 0;

    /** Constructor to initialize variables. */
    public ZombieColossus(Context context, Point screenSize) {

        colossusBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);

        xMin = 0;
        xMax = screenSize.x;
        yMin = 0;
        yMax = screenSize.y;

        genRandom = new Random();

        // Get the zombie graphic from drawable:
        Log.d("OVERRUN: Colossus", "Screen: (" + screenSize.x + "," + screenSize.y + ")");

        // a placeholder graphic
        colossusBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.zombie);
        Log.d("OVERRUN: Colossus", "Before Resize: (" +  colossusBitmap.getWidth() +","+ colossusBitmap.getHeight() + ")");

        colossusBitmap = getResizedBmp(screenSize.x/SCALE, screenSize.x/SCALE);
        Log.d("OVERRUN: Colossus", "After Resize: (" +  colossusBitmap.getWidth() +","+ colossusBitmap.getHeight() + ")");

        xCoord = genRandom.nextInt(xMax - colossusBitmap.getWidth());
        yCoord = yMin;

        detectZombie =  new Rect(xCoord, yCoord, xCoord + colossusBitmap.getWidth(), yCoord + colossusBitmap.getHeight());

        isActive = false;
    }

    @Override
    public Bitmap getResizedBmp(float newWidth, float newHeight) {
        int bmWidth = colossusBitmap.getWidth();
        int bmHeight = colossusBitmap.getHeight();
        float wScale = newWidth / bmWidth;
        float hScale = newHeight / bmHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(wScale, hScale);
        Bitmap resizedBMP = Bitmap.createBitmap(colossusBitmap, 0, 0, bmWidth, bmHeight, matrix, false);
        colossusBitmap.recycle();
        return resizedBMP;
    }

    @Override
    public void updateMovement() {

        if (yCoord + 1 < yMax) {
            yCoord += SPEED;
        } else {
            xCoord = genRandom.nextInt(xMax - colossusBitmap.getWidth());
            yCoord = yMin;
            setIsActive(true);
        }

        //adding top, left, bottom and right to the rect object
        detectZombie.left = xCoord;
        detectZombie.top = yCoord;
        detectZombie.right = xCoord + colossusBitmap.getWidth();
        detectZombie.bottom = yCoord + colossusBitmap.getHeight();

        //do something if enemy reaches bottom edge
        //such as creating new zombie and reducing survivor health
    }

    @Override
    public int getHP() {
        return HP;
    }

    @Override
    public Bitmap getBitmap() {
        return colossusBitmap;
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

    @Override
    public boolean getIsActive() {
        return isActive;
    }

    @Override
    public void setIsActive(boolean status) {
        isActive = status;
    }

    @Override
    public int getTimesHit() {
        return timesHit;
    }

    @Override
    public void addHit() {
        timesHit++;
    }
}

