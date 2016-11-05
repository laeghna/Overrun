package group7.tcss450.tacoma.uw.edu.overrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import java.util.Random;

/**
 * This class specifies the behavior for a zombie crawler enemy.
 * This is the slowest enemy.
 *
 * @author Lisa Taylor
 * @version 1 Nov 2016
 */

public class ZombieCrawler implements Zombie {

    //Zombie crawler's speed
    private static final int SPEED = 1;

    //Zombie crawler image
    private Bitmap crawlerBitmap;

    //Zombie crawler coordinates
    private int xCoord;
    private int yCoord;

    //Screen coordinate sto ensure enemy stays within screen
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

        xCoord = xMax;
        yCoord = genRandom.nextInt(yMax - crawlerBitmap.getHeight());
    }

    @Override
    public Bitmap getResizedBmp(float newWidth, float newHeight) {
        return null;
    }

    @Override
    public void updateMovement() {

        xCoord -= SPEED;

        //do something if enemy reaches left edge
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
}
