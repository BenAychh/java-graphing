/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

/**
 *
 * @author Corporate
 */
public class Tick {
    /**
     * The time of our information.
     */
    private final ZonedStockDateTime xTime;
    /**
     * The value of our information.
     */
    private final double yValue;
    /**
     * Public constructor, assigns values.
     * @param time **the time of our tick**
     * @param value **the value of our tick**
     */
    public Tick(final ZonedStockDateTime time, final double value) {
        xTime = time;
        yValue = value;
    }
    /**
     * Getter for the y-value.
     */
    public double getY() {
        return yValue;
    }
    /**
     * Getter for the x-value.
     */
    public ZonedStockDateTime getX() {
        return xTime;
    }
    /**
     *
     * @param start the ZonedStockDateTime start of our window.
     * @param pixelWeight the amount of seconds each pixel is worth.
     * @return a long for the x-value of the pixel.
     */
    public final int getXPixel(final ZonedStockDateTime start,
            final double pixelWeight) {
        return (int)
                Math.round(start.getMarketSecondsUntil(xTime) / pixelWeight);
    }
    /**
     *
     * @param bottom the start value of our window (bottom)
     * @param top the end value of our window (top
     * @param pixelHeight the height of our window.
     * @return a long for the y-value of the pixel.
     */
    public final int getYPixel(final double bottom, final double top,
            final int pixelHeight) {
        double valueHeight = top - bottom;
        return (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (yValue - bottom)));
    }
}
