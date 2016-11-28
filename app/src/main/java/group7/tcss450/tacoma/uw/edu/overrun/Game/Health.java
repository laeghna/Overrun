package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import group7.tcss450.tacoma.uw.edu.overrun.R;

/**
 * Class for survivor's health.
 *
 * @author Lisa Taylor
 * @version 22 November 2016
 */

public class Health extends GameCharacter {

    /** Constant for scaling health */
    private static final int SCALE = 25;

    /** Constant for survivor's max health. */
    private static final int MAX_HEALTH = 5;

    /** Survivor's current health. */
    private int currHealth;

    /** The bitmap for the health sprite image. */
    private Bitmap healthBitmap;

    /** Health coordinates. */
    private int xCoord;
    private int yCoord;

    /**
     * Contructor to initialize variables.
     * @param context - the context for the application this game is played from
     */
    public Health(Context context, Point screenSize) {

        currHealth = MAX_HEALTH;

        // Get the health graphic from drawable
        healthBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.brain);
        healthBitmap = getResizedBmp(healthBitmap, screenSize.x/SCALE, screenSize.x/SCALE);
        xCoord = 0;
        yCoord = 0;
    }

    public int getCurrHealth() {
        return currHealth;
    }

    public void setCurrHealth(int currHealth) {
        this.currHealth = currHealth;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public Bitmap getmBitmap() {
        return healthBitmap;
    }
}
