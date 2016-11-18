package group7.tcss450.tacoma.uw.edu.overrun.Game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

/**
 * This class represents the barrier between the Survivor and the zombies.
 * @author Leslie Pedro
 */

public class Barrier {

    /** The number of rows of blocks in the barrier. */
    public static final int BARRIER_ROWS = 5;

    /** The number of columns of blocks in the barrier. */
    public static final int BARRIER_COLS = 50;

    /** The size of the device's screen. */
    private Point mScreen;

    /** The survivor. */
    private Survivor mSurvivor;

    /** The array of blocks making up the barrier. */
    private BarrierBlock[][] mBarrier;

    /**
     * Creates a barrier between the survivor and the attacking zombies.
     * @param screenSize the size of the device's screen.
     * @param s the survivor.
     */
    public Barrier(Point screenSize, Survivor s) {
        mScreen = screenSize;
        mSurvivor = s;
        mBarrier = assembleBarrier();
    }

    /**
     * Detects a collision between the zombie, z , and the blocks in the barrier.
     * @param z the zombie.
     * @return true if the zombie is colliding with an active block in the barrier.
     */
    public boolean detectCollisions(Zombie z) {
        boolean isHit = false;
        RectF newZ = new RectF(z.getDetectZombie().left, z.getDetectZombie().top,
                z.getDetectZombie().right, z.getDetectZombie().bottom);
        for(int row = 0; row < BARRIER_ROWS; row++) {
            for(int col = 0; col < BARRIER_COLS; col++) {
                if(!mBarrier[row][col].getmIsDestroyed() && RectF.intersects(mBarrier[row][col].getmBlock(), newZ)) {
                    mBarrier[row][col].reduceHealth();
                    isHit = true;
                }
            }
            if(isHit) {
                row = BARRIER_ROWS;
            }
        }
        return isHit;
    }

    /** For checking coordinates while testing. */
    private void testBarrier() {
        for(int row = 0; row < BARRIER_ROWS; row++) {
            for(int col = 0; col < BARRIER_COLS; col++) {
                Log.d("TEST_BARRIER", mBarrier[row][col].getmBlock().toString());
            }
        }
    }

    /**
     * Assembles the barrier based on the screen size of the device being used.
     * @return a 2-d array of RectF objects making up the barrier.
     */
    public BarrierBlock[][] assembleBarrier() {
        float portionOfScreen = 1f/10;
        float barrier_height = portionOfScreen * mScreen.y;
        float rect_height = barrier_height / BARRIER_ROWS;
        float startPosY = mSurvivor.getmY() - 1 - barrier_height;
        float endPosY = mSurvivor.getmY() - 1;
        float startPosX = 1f;
        float endPosX = mScreen.x - 1;
        float rect_width = ((endPosX - startPosX)/BARRIER_COLS);
        BarrierBlock[][] barrier = new BarrierBlock[BARRIER_ROWS][BARRIER_COLS];

        float currentX = startPosX, currentY = startPosY;
        for(int i = 0; i < BARRIER_ROWS; i++) {
            currentX = startPosX;
            for(int j = 0; j < BARRIER_COLS; j++) {
                barrier[i][j] = new BarrierBlock(currentX, currentY, rect_width + currentX, currentY + rect_height);
                currentX = currentX + rect_width;
            }
            currentY = currentY + rect_height;
        }
        return barrier;
    }

    /**
     * Draws the barrier for the game.
     */
    public void drawBarrier(Paint brush, Canvas bg) {
        int oldColor = brush.getColor();
        brush.setColor(Color.GRAY);
        for(int i = 0; i < BARRIER_ROWS; i++) {
            for(int j = 0; j < BARRIER_COLS; j++) {
                if(!mBarrier[i][j].getmIsDestroyed()) {
                    bg.drawRect(mBarrier[i][j].getmBlock(), brush);
                }
            }
        }
        brush.setColor(oldColor);
    }

    /**
     * Returns true if the block passed is Destroyed.
     * @param b the block to check.
     * @return true if the bloack is destroyed, false otherwise.
     */
    public boolean getBlockIsDestroyed(BarrierBlock b) {
        return b.getmIsDestroyed();
    }

    /**
     * Private inner class for the Barrier class. Creates the smaller
     * rectangles that make up the full barrier.
     */
    private class BarrierBlock {

        /** The rectangle for this portion of the barrier. */
        private RectF mBlock;

        /** Max health for all blocks. */
        public static final int MAX_HEALTH = 500;

        /** The current health of the block. */
        private int mHealth;

        /** True if the block has been destroyed by zombies, false otherwise. */
        private boolean mIsDestroyed;

        /**
         * Constructs the block with the passed parameters.
         * @param left the left coordinate of the block.
         * @param top the top coordinate of the block.
         * @param right the right coordinate of the block.
         * @param bottom the bottom coordinate of the block.
         */
        private BarrierBlock(float left, float top, float right, float bottom) {
            mHealth = MAX_HEALTH;
            mIsDestroyed = false;
            mBlock = new RectF(left, top, right, bottom);
        }

        /**
         * Reduces the health of the block, setting mIsDestroyed to true
         * when health reaches 0.
         */
        private void reduceHealth() {
            if((mHealth - 1) > 0) {
                mHealth -= 1;
            } else {
                mIsDestroyed = true;
            }
        }

        /**
         * Gets the status of this block.
         * @return true if the block is destroyed, false otherwise.
         */
        private boolean getmIsDestroyed() {
            return mIsDestroyed;
        }

        /**
         * Gets the rectangle for this block.
         * @return the rectangle for this block.
         */
        private RectF getmBlock() {
            return mBlock;
        }

        /**
         * Sets the isDestroyed value for this block.
         * @param isDestroyed true if the block has been destroyed, false otherwise.
         */
        private void setmIsDestroyed(boolean isDestroyed) {
            mIsDestroyed = isDestroyed;
        }
    }
}
