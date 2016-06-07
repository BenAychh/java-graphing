/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author benhernandez
 */
public class FibonacciRetracements extends JComponent implements  Graphable{
  /**
   * The first candle passed.
   */
  private Candle candle1;
  /**
   * The second candle passed.
   */
  private Candle candle2;
  /**
   * the image to draw.
   */
  private BufferedImage lines;
  /**
   * The number of seconds per pixel.
   */
  private double xPixelWeight;
  /**
   * The color to draw everything but the 50% line.
   */
  private Color lineColor;
  /**
   * The color to draw the graph.
   */
  private Color imageLineColor;
  /**
   * The color to draw the special line;
   */
  private Color imageSpecialLineColor;
 
  private Color imageSpecialLineColor2;
  
  private Color imageSpecialLineColor3;
  
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
  private double topMargin = 0;
  /**
   * The top margin.
   */
  private double imageTopMargin = 0;
  /**
   * The bottom margin.
   */
  private double bottomMargin = 0;
  /**
   * The bottom margin.
   */
  private double imageBottomMargin = 0;
  /**
   * The left margin.
   */
  private double leftMargin = 0;
  /**
   * The left margin.
   */
  private double imageLeftMargin = 0;
  /**
   * The right margin.
   */
  private double rightMargin = 0;
  /**
   * The right margin.
   */
  private double imageRightMargin = 0;
  /**
   * Need a way to keep track of point changes.
   */
  private boolean candlesChanged = true;
  /**
   * The list of percentages we are displaying.
   */
  private ArrayList<Double> percentages;
  /**
   * The special percentage color.
   */
  private final double specialPercentageNegFourFifty = -450.0;
  private final double specialPercentageNegThreeFifty = -350.0;
  private final double specialPercentageNegTwoFifty = -250.0;
  private final double specialPercentageNegOneFifty = -150.0;
  private final double specialPercentageNegFifty = -50.0;
  private final double specialPercentageNegThirty = -30.9;
  private final double specialPercentageTen = 10.0;
  private final double specialPercentage = 50.0;
  private final double specialPercentageNinety = 90.0;
  private final double specialPercentageOneThirty = 130.9;
  private final double specialPercentageOneFifty = 150.0;
  private final double specialPercentageTwoFifty = 250.0;
  private final double specialPercentageThreeFifty = 350.0;
  private final double specialPercentageFourFifty = 450.0;
  /**
   * The candle grapher (needed for clicks);
   */
  private CandleGraphs cg;
  /**
   * Click counter
   */
  private int clickCounter = 0;
  /**
   * Initializer.
   * @param pCandle1 The first candle.
   * @param pCandle2 The second candle.
   */
  /**
   * Blank initializer.
   */
  public FibonacciRetracements(final CandleGraphs pCg) {
    cg = pCg;
    candle1 = null;
    candle2 = null;
    percentages = new ArrayList<>();
    percentages.add(-500.0);

    percentages.add(-450.0);

    percentages.add(-400.0);

    percentages.add(-350.0);

    percentages.add(-300.0);

    percentages.add(-250.0);

    percentages.add(-200.0);

    percentages.add(-150.0);

    percentages.add(-100.0);

    percentages.add(-50.0);
    percentages.add(-30.9);
    percentages.add(0.0);
    percentages.add(10.0);
    percentages.add(50.0);
    percentages.add(90.0);
    percentages.add(100.0);
    percentages.add(130.9);
    percentages.add(150.0);
   
    percentages.add(200.0);

    percentages.add(250.0);
    
    percentages.add(300.0);

    percentages.add(350.0);
   
    percentages.add(400.0);

    percentages.add(450.0);
   
    percentages.add(500.0);
    lineColor = Color.white;
    imageSpecialLineColor = Color.yellow;
    imageSpecialLineColor2 = Color.red;
    imageSpecialLineColor3 = Color.green;
  }
  public FibonacciRetracements(final CandleGraphs pCg,
      final Candle pCandle1, final Candle pCandle2) {
    this(pCg);
    candle1 = pCandle1;
    candle2 = pCandle2;
  }
  @Override
  public void paintComponent(final Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(drawLines(), null, 0, 0);
  }
  /**
   * Checks to see if the window has changed in any way.
   *
   * @return true if the window has changed in any way.
   */
  private boolean isChanged() {
    if (lines == null) {
      return true;
    }
    if (candlesChanged) {
      candlesChanged = false;
      return true;
    }
    if (lines.getWidth() != getWidth()
        || lines.getHeight() != getHeight()) {
      return true;
    }
    return (!imageStartTime.equals(startTime)
        || !imageEndTime.equals(endTime)
        || imageHighY != highY
        || imageLowY != lowY
        || imageTopMargin != topMargin
        || imageBottomMargin != bottomMargin
        || imageLeftMargin != leftMargin
        || imageRightMargin != rightMargin
        || imageLineColor != lineColor);
  }

  /**
   * Sets the values if we are given any new information.
   */
  private void setValues() {
    imageStartTime = startTime;
    imageEndTime = endTime;
    imageLowY = lowY;
    imageHighY = highY;
    imageTopMargin = topMargin;
    imageBottomMargin = bottomMargin;
    imageLeftMargin = leftMargin;
    imageRightMargin = rightMargin;
    imageLineColor = lineColor;
  }
  /**
   * Draws the lines fibonacci lines on the graph.
   * @return an image with the lines drawn on it.
   */
  private BufferedImage drawLines() {
    if (isChanged() && candle1 != null && candle2 != null) {
      setValues();
      BufferedImage temp = new BufferedImage(getWidth(), getHeight(),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = temp.createGraphics();
      double min = Math.min(candle1.getLow(), candle2.getLow());
      double max = Math.max(candle1.getHigh(), candle2.getHigh());
      double range = max - min;
      DecimalFormat df = new DecimalFormat("$###.00");
      percentages.forEach((percent) -> {
        if (percent != specialPercentage
            && percent !=specialPercentageNegFourFifty 
            && percent !=specialPercentageNegThreeFifty 
            && percent !=specialPercentageNegTwoFifty 
            && percent !=specialPercentageNegOneFifty 
            && percent !=specialPercentageNegThirty
            && percent !=specialPercentageNegFifty
            && percent !=specialPercentageTen
            && percent !=specialPercentageNinety
            && percent !=specialPercentageOneThirty
            && percent !=specialPercentageOneFifty 
            && percent !=specialPercentageTwoFifty
            && percent !=specialPercentageThreeFifty 
            && percent !=specialPercentageFourFifty
            ){
          g2d.setColor(imageLineColor);
        } else if (percent == specialPercentageNegThirty || percent == specialPercentageOneThirty){
          g2d.setColor(imageSpecialLineColor2);
        } else if (percent == specialPercentageTen || percent == specialPercentageNinety){
          g2d.setColor(imageSpecialLineColor3);
        } else {
          g2d.setColor(imageSpecialLineColor);
        }
        double yValue = min + range * percent / 100;
        int leftX = (int) leftMargin;
        int rightx = (int) (getWidth() - rightMargin);
        int y = getYPixel(yValue);
        g2d.drawLine(leftX, y, rightx, y);
        FontMetrics fm = g2d.getFontMetrics();
        
        String label = " " + percent.toString() + "% / " + df.format(yValue);
        int height = fm.getHeight();
        int width = fm.stringWidth(label);
        final int rightBuffer = 10;
        int textX =
            (int) (getWidth() - rightMargin - rightBuffer - width);
        int textY = y + height / 2;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(textX, textY - height, width, height);
        g2d.setColor(Color.white);
        g2d.drawString(label, textX, textY - 3);
      });
      lines = temp;
    }
    return lines;
  }
  /**
   * Returns the y pixel given a y-value.
   * @param yValue the yValue to convert to a y-pixel.
   * @return the y-pixel.
   */
  public final int getYPixel(final double yValue) {
    double valueHeight = imageHighY - imageLowY;
    double paintHeight = getHeight() - bottomMargin - topMargin;
    return (int) Math.round(paintHeight
        - (paintHeight / valueHeight * (yValue - imageLowY)) + topMargin);
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

  /**
   * Sets the text location.
   * @param x the x pixel of the text location
   * @param y the y pixel of the text location
   * @param fromBottom Should we count from the bottom?
   * @param fromLeft Should we count from the left?
   */
  @Override
  public final void setTextLocation(final int x, final int y,
      final boolean fromBottom, final boolean fromLeft) {
    System.out.println("No Text to display");
  }

  @Override
  public final void setMargins(final int top, final int bottom, final int left,
      final int right) {
    topMargin = top;
    bottomMargin = bottom;
    leftMargin = left;
    rightMargin = right;
  }

  @Override
  public final void setMouseMotionLocation(Point p) {
    if (clickCounter == 1) {
      candle2 = cg.getCurrentMouseCandle(p);
      candlesChanged = true;
      repaint();
    }
  }
  
  public final void setMouseClickLocation(Point p) {
    if (clickCounter != -1) {
      clickCounter++;
      Candle tempCandle = cg.getCurrentMouseCandle(p);
      if (clickCounter == 1) {
        candle1 = tempCandle;
      } else {
        clickCounter = -1;
        candle2 = tempCandle;
        repaint();
        candlesChanged = true;
      }
    }
  }
  public void startMappingCandles() {
    clickCounter = 0;
  }
  public void clear() {
    clickCounter = -1;
    candle1 = null;
    candle2 = null;
    lines = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    repaint();
  }
}
