/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.candhcapital.Graphing;

import java.awt.Color;
import java.awt.Rectangle;
import java.text.DecimalFormat;

/**
 *
 * @author Corporate
 */
public class Block {
    /**
     * Need to know if red for different brightener.
     */
    private boolean red;
    /**
     * The start time.
     */
    private ZonedStockDateTime beginning;
    /**
     * The value of our block.
     */
    private double value;
    /**
     * The color of the block (based on candles).
     */
    private final Color blockColor;
    /**
     * The rectangle we are going to draw.
     */
    private Rectangle block;
    /**
     * Needs a way to see if we can graph this.
     */
    private boolean gotBlock = false;
    /**
     * A formatted string with our volume.
     */
    private final String formattedValue;
    /**
     * The Candle UP color.
     */
    private static final Color CANDLEUPCOLOR = new Color(0, 153, 0);
    /**
     * The Candle NEUTRAL color.
     */
    private static final Color CANDLENEUTRALCOLOR = new Color(255, 255, 0);
    /**
     * The Candle DOWN color.
     */
    private static final Color CANDLEDOWNCOLOR = new Color(204, 0, 0);
    /**
     * Takes a block and sets the color.
     * @param start the time this block starts at.
     * @param pDifference the difference of the last volume and first.
     * @param pColor a double so we know what color to make this block.
     */
    public Block(final ZonedStockDateTime start,
            final double pvalue, final double pColor) {
        beginning = start;
        DecimalFormat formatter = new DecimalFormat("#,###");
        formattedValue = "Volume: " + formatter.format(pvalue) + "M";
        value = pvalue;
        if (pColor > 0) {
            blockColor = CANDLEUPCOLOR;
        } else if (pColor == 0) {
            blockColor = CANDLENEUTRALCOLOR;
        } else {
            blockColor = CANDLEDOWNCOLOR;
            red = true;
        }
    }
    /**
     * Once we know the dimensions of the window we can calculate the location
     * of the rectangle we are going to draw.
     * @param start The start time of our window.
     * @param pixelWeight The number of seconds per pixel.
     * @param candleWidth The width of our candles.
     * @param bottom The lowy value.
     * @param top The highy value.
     * @param pixelHeight The number of pixels of our graph.
     */
    public final void setRectangles(final ZonedStockDateTime start,
            final double pixelWeight, final int candleWidth,
            final double bottom, final double top, final int pixelHeight,
            final int topMargin) {
        int x1 = (int)
                Math.round(start.getMarketSecondsUntil(beginning)
                        / pixelWeight);
        ZonedStockDateTime end = beginning.getCopy();
        end.addSeconds(candleWidth);
        int x2 = (int)
                Math.round(start.getMarketSecondsUntil(end) / pixelWeight) - 1;
        double valueHeight = top - bottom;
        int y1 = (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (0 - bottom))) + topMargin;
        int y2 = (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (value - bottom))) + topMargin;
        if (y1 - y2 >= 0) {
            block = new Rectangle(x1, y2, x2 - x1, y1 - y2);
        } else {
            block = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        }
        gotBlock = true;
    }
    /**
     * public block's start X value.
     * @return **the x-value to start at**
     */
    public final int getX() {
        if (gotBlock) {
            return (int) block.getX();
        } else {
            return -1;
        }
    }
    /**
     * public block's start Y value.
     * @return **the y-value to start at**
     */
    public final int getY() {
        if (gotBlock) {
            return (int) block.getY();
        } else {
            return -1;
        }
    }
    /**
     * public block's width.
     * @return **the width of our block**
     */
    public final int getWidth() {
        if (gotBlock) {
            return (int) block.getWidth();
        } else {
            return -1;
        }
    }
    /**
     * public block's height.
     * @return **the height of our block**
     */
    public final int getHeight() {
        if (gotBlock) {
            return (int) block.getHeight();
        } else {
            return -1;
        }
    }
    /**
     * Gets the color of our block.
     * @return **the color of our block**
     */
    public final Color getColor() {
        return blockColor;
    }
    /**
     * Checks to see if our candle should be red.
     * @return true for red.
     */
    public final boolean isRed() {
        return red;
    }
    /**
     * Checks to see if the mouse is in that block.
     * @param x **The x-value our mouse is currently at**
     * @return **True if the mouse is contained**
     */
    public final boolean matchMouseX(final int x) {
        return (x >= getX() && x <= getX() + getWidth());
    }
    /**
     * simple toString() function.
     * @return **The value formatted with commas**
     */
    @Override
    public final String toString() {
        return formattedValue;
    }
    public final double getValue() {
        return value;
    }
}
