/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import net.candhcapital.Graphing.Graphable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;

/**
 *
 * @author Corporate
 */
public class CandleGraphs extends JComponent implements ComponentListener,
    Graphable {
  /**
   * For text formatting, need to know if these are percents.
   */
  private boolean percent = false;
  /**
   * Y-Location to draw Value Text.
   */
  private int textY = 0;
  /**
   * X-Location to draw Value Text.
   */
  private int textX = 0;
  /**
   * Draw the text from the bottom.
   */
  private boolean fromBottom = true;
  /**
   * Draw the text from the left.
   */
  private boolean fromLeft = true;
  /**
   * the width in seconds of our candles.
   */
  private int candleWidth = 0;
  /**
   * The currently drawn image start time.
   */
  private ZonedStockDateTime imageStartTime;
  /**
   * The value corresponding to the 0th pixel of our window.
   */
  private ZonedStockDateTime startTime;
  /**
   * The currently drawn image time end.
   */
  private ZonedStockDateTime imageEndTime;
  /**
   * The value corresponding to the last pixel of our window.
   */
  private ZonedStockDateTime endTime;
  /**
   * the xLocation of the mouse.
   */
  private int mouseX;
  /**
   * a mapping of pixels for mouse matching/contains functions.
   */
  private HashMap<Integer, Integer> pixelMap;
  /**
   * The color to draw the graph.
   */
  private Color lineColor;
  /**
   * The color to draw the graph.
   */
  private Color imageLineColor;
  /**
   * Unhighlighted alpha.
   */
  private static final int LOWLIGHTALPHA = 150;
  /**
   * The currently drawn image low y.
   */
  private double imageLowY;
  /**
   * The value corresponding to the bottom pixel of our window.
   */
  private double lowY;
  /**
   * The currently drawn image high y.
   */
  private double imageHighY;
  /**
   * The value corresponding to the top pixel of our window.
   */
  private double highY;
  /**
   * The top margin.
   */
  private int topMargin = 0;
  /**
   * The top margin.
   */
  private int imageTopMargin = 0;
  /**
   * The bottom margin.
   */
  private int bottomMargin = 0;
  /**
   * The bottom margin.
   */
  private int imageBottomMargin = 0;
  /**
   * The left margin.
   */
  private int leftMargin;
  /**
   * The left margin.
   */
  private int imageLeftMargin;
  /**
   * The right margin.
   */
  private int rightMargin;
  /**
   * The right margin.
   */
  private int imageRightMargin;
  /**
   * Need a way to keep track of point changes.
   */
  private boolean pointsChanged = true;
  /**
   * Keeps track of if the line should be highlighted or not.
   */
  private boolean highlighted = false;
  /**
   * locking object for changing highlights.
   */
  private final Object highlightLock = new Object();
  /**
   * The image with the regular line.
   */
  private BufferedImage candleImage;
  /**
   * Display graph.
   */
  private boolean displayCandles = true;
  /**
   * Just for syncing.
   */
  private final Object candleLocker = new Object();
  /**
   * A way to specify which candle type we want.
   */
  public enum CANDLETYPE {
    /**
     * Regular candles.
     */
    regular,
    /**
     * Heikin Ashi candles.
     */
    heikinashi
  };
  /**
   * The Candle UP color.
   */
  private Color candleUpColor = new Color(0, 153, 0);
  /**
   * The Candle UP color.
   */
  private Color imageCandleUpColor = new Color(0, 153, 0);
  /**
   * The Candle UP color.
   */
  private Color candleNeutralColor = new Color(255, 255, 0);
  /**
   * The Candle UP color.
   */
  private Color imageCandleNeutralColor = new Color(255, 255, 0);
  /**
   * The Candle DOWN color.
   */
  private Color candleDownColor = new Color(204, 0, 0);
  /**
   * The Candle DOWN color.
   */
  private Color imageCandleDownColor = new Color(204, 0, 0);
  /**
   * The Candle WICK color.
   */
  private final Color candleWickColor = Color.white;
  /**
   * The Candle WICK color.
   */
  private Color imageCandleWickColor = Color.white;
  /**
   * The open, close and high and low information for each time section.
   */
  private ArrayList<Candle> candles;
  /**
   * The open, close and high and low information for each time section.
   */
  private ArrayList<Candle> heikenashiCandles;
  /**
   * The candle types, so far regular or heiken-ashi.
   */
  private CANDLETYPE candletype = CANDLETYPE.regular;
  /**
   * The candle types, so far regular or heiken-ashi.
   */
  private CANDLETYPE imageCandletype = CANDLETYPE.regular;

  /**
   *
   */
  public CandleGraphs() {
    candleImage = new BufferedImage(1, 1,
        BufferedImage.TYPE_INT_ARGB);
    candles = new ArrayList<>();
    heikenashiCandles = new ArrayList<>();
  }

  /**
   * Calls drawGraph so it can paint the bufferedImage.
   *
   * @param g **the graphics**
   */
  @Override
  protected final void paintComponent(final Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(drawCandles(), null, 0, 0);
    synchronized (candleLocker) {
      if (mouseX > leftMargin && mouseX <= getWidth() - rightMargin) {
        for (Candle candle : candles) {
          if (candle.matchMouseX(mouseX)) {
            g.setFont(new Font("Calibri", Font.PLAIN, 16));
            RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHints(rh);
            g2d.setColor(Color.white);
            int leftLocation = (int) (textX + imageLeftMargin);
            if (!fromLeft) {
              leftLocation = getWidth() - leftLocation;
            }
            int topLocation = textY + imageBottomMargin
                + g2d.getFontMetrics().getHeight();
            if (fromBottom) {
              topLocation = (int) (getHeight() - topLocation);
            }
            g2d.drawString(candle.toString(percent), leftLocation, topLocation);
            Color barInterior;
            Color barBorder;
            final int maxBrighten = 4;
            int brightener = maxBrighten / 2;
            if (candle.isGreen()) {
              barBorder = imageCandleUpColor;
            } else if (candle.isYellow()) {
              barBorder = imageCandleNeutralColor;
            } else {
              barBorder = imageCandleDownColor;
              brightener = maxBrighten;
            }
            barInterior = new Color(barBorder.getRed(),
                barBorder.getGreen(), barBorder.getBlue(),
                100);
            for (int j = 1; j <= brightener; j++) {
              barInterior = barInterior.brighter();
              barBorder = barBorder.brighter();
            }
            int alpha = 150;
            if (brightener == 4) {
              alpha = 175;
            }
            barInterior = new Color(barInterior.getRed(),
                barInterior.getGreen(), barInterior.getBlue(),
                alpha);
            g2d.setColor(barInterior);
            g2d.fillRect(candle.getBody().x, candle.getBody().y,
                candle.getBody().width,
                candle.getBody().height);
          }
        }
      }
    }
  }

  public BufferedImage drawCandles() {
    if (displayCandles) {
      if (isChanged()) {
        setValues();
        BufferedImage temp = new BufferedImage(getWidth(), getHeight(),
            BufferedImage.TYPE_INT_ARGB);
        ArrayList<Candle> tempCandles;
        if (imageCandletype == CANDLETYPE.regular) {
          tempCandles = new ArrayList<>(candles);
        } else {
          tempCandles = new ArrayList<>(heikenashiCandles);
        }
        float thickness = 2;
        Graphics2D g2d = (Graphics2D) temp.getGraphics();
        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
        g2d.setStroke(new BasicStroke(thickness));
        for (Candle ci : tempCandles) {
          double xPixelValue
              = (double) (imageStartTime.getSecondsBetween(imageEndTime))
              / (temp.getWidth() - imageLeftMargin - imageRightMargin);
          int imgHeight = (int) (temp.getHeight() - imageTopMargin
              - imageBottomMargin);
          ci.setRectangles(startTime, xPixelValue, candleWidth,
              imageLowY, imageHighY, imgHeight, imageTopMargin);
          if (ci.coordinatesSet()) {
            Color barInterior;
            Color barBorder;
            if (ci.isGreen()) {
              barBorder = imageCandleUpColor;
            } else if (ci.isYellow()) {
              barBorder = imageCandleNeutralColor;
            } else {
              barBorder = imageCandleDownColor;
            }
            barInterior = new Color(barBorder.getRed(),
                barBorder.getGreen(), barBorder.getBlue(),
                100);
            g2d.setColor(barInterior);
            g2d.fillRect(ci.getBody().x, ci.getBody().y,
                ci.getBody().width, ci.getBody().height);
            g2d.setColor(barBorder);
            g2d.drawRect(ci.getBody().x, ci.getBody().y,
                ci.getBody().width, ci.getBody().height);
            g2d.setColor(candleWickColor);
            g2d.fillRect(ci.getTopWick().x, ci.getTopWick().y,
                ci.getTopWick().width, ci.getTopWick().height);
            g2d.fillRect(ci.getBottomWick().x, ci.getBottomWick().y,
                ci.getBottomWick().width,
                ci.getBottomWick().height);
          }
        }
        candleImage = temp;
      }

    } else {

    }
    return candleImage;
  }

  public void calculateCandles(ArrayList<Candle> tempCandles) {
    candleWidth = (int) tempCandles.get(0).getStart()
        .getSecondsBetween(tempCandles.get(1).getStart());
    pointsChanged = true;
    ArrayList<Candle> tempHeiken = new ArrayList<>();
    for (Candle c : tempCandles) {
      double rOpen = c.getOpen();
      double rClose = c.getClose();
      double rLow = c.getLow();
      double rHigh = c.getHigh();
      double hClose = 0.0;
      double hOpen = 0.0;
      double hLow = 0.0;
      double hHigh = 0.0;
      hClose = (rOpen + rHigh + rLow + rClose) / 4;
      if (tempHeiken.size() == 0) {
        tempHeiken.add(new Candle(c.getStart(), rLow, rHigh, rOpen,
            hClose));
      } else {
        int lastIndex = tempHeiken.size() - 1;
        Candle lastCI = tempCandles.get(lastIndex);
        Candle lastHA = tempHeiken.get(lastIndex);
        hOpen = (lastHA.getOpen() + lastHA.getClose()) / 2;
        hLow = Math.max(Math.max(rHigh, hOpen), hClose);
        hHigh = Math.min(Math.min(rLow, hOpen), hClose);
        tempHeiken.add(new Candle(c.getStart(), hLow, hHigh, hOpen,
            hClose));
      }
    }
    synchronized (candleLocker) {
      candles.clear();
      heikenashiCandles.clear();
      candles = new ArrayList<>(tempCandles);
      heikenashiCandles = new ArrayList<>(tempHeiken);
    }
    repaint();
  }

  /**
   * Sets the margins for our window.
   *
   * @param top
   * @param bottom
   * @param left
   * @param right
   */
  public final void setMargins(final int top, final int bottom,
      final int left, final int right) {
    topMargin = top;
    bottomMargin = bottom;
    leftMargin = left;
    rightMargin = right;
  }

  /**
   * Sets the line color.
   *
   * @param color **the color to paint the line**
   */
  public final void setColor(final Color color) {
    lineColor = color;
  }

  /**
   * Whether the graph should be highlighted or not.
   *
   * @param highlight **true or false**
   */
  public final void setHighlighted(final boolean highlight) {
    this.highlighted = highlight;
  }

  /**
   * Sets the time corresponding to the 0th pixel.
   *
   * @param zsdt **the beginning time of our window**
   */
  @Override
  public final void setTimeStart(final ZonedStockDateTime zsdt) {
    this.startTime = zsdt;
  }

  /**
   * Sets the time corresponding to the last pixel.
   *
   * @param zsdt **The new final time value of the window**
   */
  @Override
  public final void setTimeEnd(final ZonedStockDateTime zsdt) {
    this.endTime = zsdt;
  }

  /**
   * Sets the bottom of the y-range.
   *
   * @param pLowY **this is the new bottom y-value**
   */
  @Override
  public final void setLowY(final double pLowY) {
    this.lowY = pLowY;
  }

  /**
   * Sets the top of the y-range.
   *
   * @param pHighY **this is the new top y-value**
   */
  @Override
  public final void setHighY(final double pHighY) {
    this.highY = pHighY;
  }

  @Override
  public final void setTextLocation(final int x, final int y,
      final boolean pFromBottom, final boolean pFromLeft) {
    textX = x;
    textY = y;
    fromBottom = pFromBottom;
    fromLeft = pFromLeft;
  }

  /**
   * Tells us if these candles are in percent format.
   * @param pPercent true or false.
   */
  public final void setPercent (final boolean pPercent) {
    percent = pPercent;
  }
  /**
   * Checks to see if the window has changed in any way.
   *
   * @return true if the window has changed in any way.
   */
  private boolean isChanged() {
    if (pointsChanged) {
      pointsChanged = false;
      return true;
    }
    if (candleImage.getWidth() != getWidth()
        || candleImage.getHeight() != getHeight()) {
      return true;
    }
    return (imageHighY != highY
        || imageLowY != lowY
        || imageTopMargin != topMargin
        || imageBottomMargin != bottomMargin
        || imageLeftMargin != leftMargin
        || imageRightMargin != rightMargin
        || candletype != imageCandletype
        || candleUpColor != imageCandleUpColor
        || candleDownColor != imageCandleDownColor
        || candleWickColor != imageCandleWickColor
        || candletype != imageCandletype);
  }

  /**
   * Sets the values if we are given any new information.
   */
  private void setValues() {
    imageLowY = lowY;
    imageHighY = highY;
    imageTopMargin = topMargin;
    imageBottomMargin = bottomMargin;
    imageLeftMargin = leftMargin;
    imageRightMargin = rightMargin;
    imageLineColor = lineColor;
    imageCandletype = candletype;
    imageCandleUpColor = candleUpColor;
    imageCandleDownColor = candleDownColor;
    imageCandleWickColor = candleWickColor;
    imageCandletype = candletype;
    imageStartTime = startTime;
    imageEndTime = endTime;
  }

  @Override
  public void componentResized(ComponentEvent ce) {
    setBounds(ce.getComponent().getBounds());
  }

  @Override
  public void componentMoved(ComponentEvent ce) {

  }

  @Override
  public void componentShown(ComponentEvent ce) {

  }

  @Override
  public void componentHidden(ComponentEvent ce) {

  }

  @Override
  public void setMouseMotionLocation(Point me) {
    mouseX = (int) me.getX();
    repaint();
  }
  
  @Override
  public void setMouseClickLocation(Point me) {
    
  }
  /**
   * Gets the candle that the mouse is on right now.
   * @param me the mouse location
   * @return the mouse-highlighted candle.
   */
  public final Candle getCurrentMouseCandle(Point me) {
    for (Candle candle : candles) {
      if (candle.matchMouseX((int) me.getX())) {
        return candle;
      }
    }
    return null;
  }

}
