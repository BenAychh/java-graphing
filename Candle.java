/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author ben
 */
public class Candle {
    /**
     * The lowest value in a time frame.
     */
    private double low;
    /**
     * The highest value in a time frame.
     */
    private double high;
    /**
     * the opening value of a time frame.
     */
    private double open;
    /**
     * the closing value of a time frame.
     */
    private double close;
    private boolean gotTopWick = false;
    private boolean gotBottomWick = false;
    private boolean gotBody = false;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int wickHeight;
    private int wickDrop;
    private boolean highlighted;
    private Rectangle topWick;
    private Rectangle body;
    private Rectangle bottomWick;
    private ZonedStockDateTime beginning;
    /**
     * Simple constructor to set all values.
     * @param pLow low value
     * @param pHigh high value
     * @param pOpen opening value
     * @param pClose close value
     */
    public Candle(ZonedStockDateTime pBeginning, final double pLow,
            final double pHigh, final double pOpen, final double pClose) {
        beginning = pBeginning;
        low = pLow;
        high = pHigh;
        open = pOpen;
        close = pClose;
    }

    /**
     * @return the low value
     */
    public final double getLow() {
        return low;
    }
    /**
     * @param pLow sets low value, not sure if needed.
     */
    public final void setLow(final double pLow) {
        this.low = pLow;
    }
    /**
     * @return the high value
     */
    public final double getHigh() {
        return high;
    }
    /**
     * @param pHigh sets high value, see lowvalue.
     */
    public final void setHigh(final double pHigh) {
        this.high = pHigh;
    }
    /**
     * @return the open value.
     */
    public final double getOpen() {
        return open;
    }
    /**
     * @param pOpen sets the open value.
     */
    public final void setOpen(final double pOpen) {
        this.open = pOpen;
    }
    /**
     * @return the close value.
     */
    public final double getClose() {
        return close;
    }
    /**
     * @param pClose sets the close value.
     */
    public final void setClose(final double pClose) {
        this.close = pClose;
    }
    /**
     * @return the beginning candle time.
     */
    public final ZonedStockDateTime getStart() {
        return beginning;
    }
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
        double theTop = Math.max(close, open);
        double theBottom = Math.min(close, open);        
        int y1 = (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (theTop - bottom)));
        int y2 = (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (theBottom - bottom)));
        int wickH = (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (high - bottom)));
        int wickD = (int) Math.round(pixelHeight
                - (pixelHeight / valueHeight * (low - bottom)));
        body = new Rectangle(x1, y1 + topMargin, x2 - x1, y2 - y1);
        gotBody = true;
        topWick = new Rectangle((x1 + x2) / 2 - 1, wickH + topMargin,
                3, y1 - wickH);
        gotTopWick = true;
        bottomWick = new Rectangle((x1 + x2) / 2 - 1, y2 + topMargin,
                3, wickD - y2);
        gotBottomWick = true;
    }
    public final boolean isGreen() {
        return (close > open);
    }
    public final boolean isYellow() {
        return (close == open);
    }
    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Rectangle getTopWick() {
        return topWick;
    }

    public void setTopWick(Rectangle topWick) {
        this.topWick = topWick;
        gotTopWick = true;
    }

    public Rectangle getBody() {
        return body;
    }

    public void setBody(Rectangle body) {
        this.body = body;
        gotBody = true;
    }

    public Rectangle getBottomWick() {
        return bottomWick;
    }
    
    public void setBottomWick(Rectangle bottomWick) {
        this.bottomWick = bottomWick;
        gotBottomWick = true;
    }
    public boolean coordinatesSet() {
        return (gotTopWick && gotBody && gotBottomWick);
    }
    public final Rectangle getSize() {
        int x = body.x;
        int y = topWick.y;
        int width = body.width;
        int height = topWick.height + body.height + bottomWick.height;
        return new Rectangle(x, y, width, height);
    }
    public final Rectangle getHighlightedSize() {
        int x = body.x - 2;
        int y = topWick.y - 2;
        int width = body.width + 4;
        int height = topWick.height + body.height + bottomWick.height + 4;
        return new Rectangle(x, y, width, height);
    }
    public boolean contains(int x, int y) {
        double x1 = (double) x;
        double y1 = (double) y;
        if (topWick.contains(x1, y1) || bottomWick.contains(x1, y1) || body.contains(x1, y1))
        {
            highlighted = true;
            return true;
        } else {
            highlighted = false;
            return false;
        }
    }

  /**
   * Returns a formatted string of the candle.
   * @param percent should we display percent.
   * @return formatted candle String.
   */
  public final String toString(final boolean percent) {
      DecimalFormat formatter
          = (DecimalFormat) NumberFormat.getCurrencyInstance();
      if (percent) {
        formatter = new DecimalFormat("0.00");
        return "O: " + formatter.format(open) + "%"
                + "     C: " + formatter.format(close) + "%"
                + "     L: " + formatter.format(low) + "%"
                + "     H: " + formatter.format(high) + "%";
      }
      return "O: " + formatter.format(open)
                + "     C: " + formatter.format(close)
                + "     L: " + formatter.format(low)
                + "     H: " + formatter.format(high);
    }
    @Override
    public final String toString() {
      return toString(false);
    }
    /**
     * Checks to see if this is a match for the mouse.
     * @param x the xLocation of the mouse.
     * @return true or false.
     */
    public boolean matchMouseX(int x) {
        return (x >= body.getX() && x <= body.getX() + body.getWidth());
    }
}