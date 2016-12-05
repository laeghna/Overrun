package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * This class specifies the behavior for a zombie walker enemy.
 * This is the normal enemy.
 *
 * @author Lisa Taylor
 * @version 02 December 2016
 */

public class ZombieWalker extends BitmapResizer implements Zombie {

    /** Zombie's hit points - the shots needed to destroy zombie. */
    private static final int HP = 2;

    /** Zombie's point value for adding to the game score. */
    private static final int POINTS = 15;

    /** Zombie crawler's speed. */
    private static final int SPEED = 2;

    /** Constant for scaling zombie walker. */
    private static final int SCALE = 15;

    /** Zombie crawler image. */
    private Bitmap walkerBitmap;

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

    /** Boolean to determine if zombie reahed bottom. */
    private boolean hasReachedBottom;

    /** The number of times the zombie has been hit by a bullet. */
    private int timesHit = 0;

    /** Constructor to initialize variables. */
    public ZombieWalker(Context context, Point screenSize) {

        xMin = 0;
        xMax = screenSize.x;
        yMin = 0;
        yMax = screenSize.y;

        genRandom = new Random();

        walkerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.walker);
        walkerBitmap = getResizedBmp(walkerBitmap, screenSize.x/SCALE, screenSize.x/SCALE);

        xCoord = genRandom.nextInt(xMax - walkerBitmap.getWidth());
        yCoord = yMin;

        detectZombie =  new Rect(xCoord, yCoord, xCoord + walkerBitmap.getWidth(), yCoord + walkerBitmap.getHeight());

        isActive = false;
        hasReachedBottom = false;
    }

    @Override
    public void updateMovement() {

        if (yCoord + 1 < yMax) {
            yCoord += SPEED;

            //adding top, left, bottom and right to the rect object
            detectZombie.left = xCoord;
            detectZombie.top = yCoord;
            detectZombie.right = xCoord + walkerBitmap.getWidth();
            detectZombie.bottom = yCoord + walkerBitmap.getHeight();

        } else {

            isActive = false;
            hasReachedBottom = true;
            resetZombie();
        }
    }

    @Override
    public int getHP() {
        return HP;
    }

    @Override
    public Bitmap getBitmap() {
        return walkerBitmap;
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
    public boolean getHasReachedBottom() {
        return hasReachedBottom;
    }

    @Override
    public int getTimesHit() {
        return timesHit;
    }

    @Override
    public void addHit() {
        timesHit++;
    }

    @Override
    public int getPointValue() {
        return POINTS;
    }

    @Override
    public void resetZombie() {

        xCoord = genRandom.nextInt(xMax - walkerBitmap.getWidth());
        yCoord = yMin;
        detectZombie.left = xCoord;
        detectZombie.top = yCoord;
        detectZombie.right = xCoord + walkerBitmap.getWidth();
        detectZombie.bottom = yCoord + walkerBitmap.getHeight();
        isActive = false;
        hasReachedBottom = false;
    }
}

