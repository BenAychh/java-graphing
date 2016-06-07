/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.Point;

/**
 *
 * @author Corporate
 */
public interface Graphable {
    public void setTextLocation(int x, int y, boolean fromBottom,
            boolean fromLeft);
    /**
     * Sets the bottom of the y-range.
     * @param pLowY **this is the new bottom y-value**
     */
    public void setLowY(final double pLowY);
    /**
     * Sets the top of the y-range.
     * @param pHighY **this is the new top y-value**
     */
    public void setHighY(final double pHighY);
    /**
     * Sets the time corresponding to the 0th pixel.
     * @param zsdt **the beginning time of our window**
     */
    public void setTimeStart(final ZonedStockDateTime zsdt);
    /**
     * Sets the time corresponding to the last pixel.
     * @param zsdt **The new final time value of the window**
     */
    public void setTimeEnd(final ZonedStockDateTime zsdt);
    /**
     * Sets the margins for our window.
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public void setMargins(int top, int bottom, int left, int right);
    /**
     * Receives the mouse location;
     * @param p 
     */
    public void setMouseMotionLocation(Point p);
    /**
     * Receives the mouse clicking events.
     */
    public void setMouseClickLocation(Point p);
}
