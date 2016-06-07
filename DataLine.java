/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;
import javax.swing.JComponent;

/**
 * Graphs a set of Points.
 *
 * @author Ben
 */
public class DataLine extends JComponent implements ComponentListener,
    Graphable {
  /**
   * The number of seconds per pixel.
   */
  double xPixelWeight;
  /**
   * How to format the value.
   */
  private DecimalFormat formatter;
  /**
   * The value of our graph at the specific mouse point.
   */
  private String value = "";
  /**
   * Display the time in the value string.
   */
  private boolean displayTime;
  /**
   * Display the value in the value string.
   */
  private boolean displayValue;
  /**
   * Display the name in the value string;
   */
  private boolean displayName;
  /**
   * The name of our line;
   */
  private String name;
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
   * The circle which follows the line with the mouse.
   */
  private Ellipse2D.Double mouseLocationCircle;
  /**
   * The diameter of the circle that follows the line;
   */
  private final int circleDiameter = 10;
  /**
   * How close the mouse has to be to the line to highlight it.
   */
  private int highlightRadius = 12;
  /**
   * old mouse X.
   */
  private int oldMouseX = 0;
  /**
   * the xLocation of the mouse.
   */
  private int mouseX;
  /**
   * a mapping of pixels to values.
   */
  private NavigableMap<Integer, Double> valueMap;
  /**
   * a mapping of pixels for mouse matching/contains functions.
   */
  private NavigableMap<Integer, Integer> pixelMap;
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
  private double leftMargin;
  /**
   * The left margin.
   */
  private double imageLeftMargin;
  /**
   * The right margin.
   */
  private double rightMargin;
  /**
   * The right margin.
   */
  private double imageRightMargin;
  /**
   * The list of our points.
   */
  private ArrayList<Tick> points;
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
  private BufferedImage regularLine;
  /**
   * The image with the highlighted line.
   */
  private BufferedImage highlightedLine;

  /**
   * Constructor.
   */
  public DataLine() {
    points = new ArrayList<>();
    name = "";
    formatter = new DecimalFormat("###,###,###.000");
    displayTime = true;
    displayValue = true;
    displayName = true;
    mouseLocationCircle = new Ellipse2D.Double(-circleDiameter,
        -circleDiameter, circleDiameter, circleDiameter);
    regularLine = new BufferedImage(1, 1,
        BufferedImage.TYPE_INT_ARGB);
    highlightedLine = new BufferedImage(1, 1,
        BufferedImage.TYPE_INT_ARGB);
    pixelMap = new TreeMap<>();
    valueMap = new TreeMap<>();
  }

  /**
   * Calls drawGraph so it can paint the bufferedImage.
   *
   * @param g **the graphics**
   */
  @Override
  protected final void paintComponent(final Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.drawImage(drawGraph(), null, 0, 0);
    //if (pixelMap.containsKey(mouseX)) {
    g2d.setColor(imageLineColor);
    RenderingHints rh = new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHints(rh);
    g2d.fill(mouseLocationCircle);
    //}
//    if (highlighted) {
//      g2d.setColor(imageLineColor);
//    } else {
//      g2d.setColor(imageLineColor.darker());
//    }
    final int fontSize = 16;
    g.setFont(new Font("Calibri", Font.PLAIN, fontSize));
    int leftLocation = (int) (textX + imageLeftMargin);
    if (!fromLeft) {
      leftLocation = getWidth() - leftLocation;
    }
    int topLocation = (int) (textY + imageBottomMargin
        + g2d.getFontMetrics().getHeight());
    if (fromBottom) {
      topLocation = (int) (getHeight() - topLocation);
    }
    g2d.drawString(value, leftLocation, topLocation);
  }

  /**
   * Sets the margins for our window.
   *
   * @param top the top margin.
   * @param bottom the bottom margin.
   * @param left the left margin.
   * @param right the right margin.
   */
  @Override
  public final void setMargins(final int top, final int bottom, final int left,
      final int right) {
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

  /**
   * Updates the points to be drawn.
   *
   * @param pPoints **the array of points to be drawn**
   */
  public final void setPoints(final ArrayList<Tick> pPoints) {
    pointsChanged = true;
    this.points = pPoints;
    repaint();
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
   * Should our value string display the value.
   *
   * @param display true or false.
   */
  public final void displayValue(boolean display) {
    displayValue = display;
  }

  /**
   * Should our value string display the time.
   *
   * @param display true or false.
   */
  public final void displayTime(boolean display) {
    displayTime = display;
  }

  /**
   * Should our value string display the name.
   *
   * @param display true or false.
   */
  public final void displayName(boolean display) {
    displayName = display;
  }

  public final void setDisplayName(String s) {
    name = s;
  }

  /**
   * Sets the formatter for our values in our display.
   *
   * @param s the string format.
   */
  public final void setFormatter(String s) {
    formatter = new DecimalFormat(s);
  }
  public final ArrayList<Tick> getPoints() {
    return points;
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
    if (highlighted) {
      if (highlightedLine.getWidth() != getWidth()
          || highlightedLine.getHeight() != getHeight()) {
        return true;
      }
    } else {
      if (regularLine.getWidth() != getWidth()
          || regularLine.getHeight() != getHeight()) {
        return true;
      }
    }
    return (!imageStartTime.equals(startTime)
        || !imageEndTime.equals(endTime)
        || imageHighY != highY
        || imageLowY != lowY
        || imageTopMargin != topMargin
        || imageBottomMargin != bottomMargin
        || imageLeftMargin != leftMargin
        || imageRightMargin != rightMargin
        || imageLineColor != lineColor
        || regularLine.getWidth() != getWidth()
        || regularLine.getHeight() != getHeight());
  }

  /**
   * Sets the values if we are given any new information.
   */
  private void setValues() {
    valueMap.clear();
    pixelMap.clear();
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
   * Draws the graph if needed and returns the correct image.
   *
   * @return A regular or highlighted line.
   */
  private BufferedImage drawGraph() {
    if (isChanged() && points.size() > 0) {
      setValues();
      DateTimeFormatter dtf
          = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm");
      BufferedImage temp = new BufferedImage(getWidth(), getHeight(),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = (Graphics2D) temp.getGraphics();
      RenderingHints rh = new RenderingHints(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHints(rh);
      g2d.setStroke(new BasicStroke(2));
      BufferedImage tempHighlight = new BufferedImage(getWidth(),
          getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2dh = (Graphics2D) tempHighlight.getGraphics();
      g2dh.setRenderingHints(rh);
      g2dh.setStroke(new BasicStroke(2));
      xPixelWeight = (double) (imageStartTime.
          getMarketSecondsUntil(imageEndTime))
          / (temp.getWidth() - imageLeftMargin - imageRightMargin);
      int imgHeight = (int) (temp.getHeight() - imageTopMargin
          - imageBottomMargin);
      Tick oldPoint = points.get(0);
      g2d.setColor(imageLineColor);
      for (int i = 1; i < points.size(); i++) {
        ZonedStockDateTime currentTime = points.get(i).getX();
        if (currentTime.isBefore(imageStartTime)) {
          oldPoint = points.get(i);
        } else if (!currentTime.isAfter(imageEndTime)) {
          int x1 = oldPoint.getXPixel(imageStartTime, xPixelWeight);
          x1 += imageLeftMargin;
          int y1 = oldPoint.getYPixel(lowY, highY, imgHeight);
          y1 += imageTopMargin;
          if (i == 1) {
            pixelMap.put(x1, y1);
            valueMap.put(x1, oldPoint.getY());
          }
          oldPoint = points.get(i);
          int x2 = oldPoint.getXPixel(imageStartTime, xPixelWeight);
          x2 += imageLeftMargin;
          int y2 = oldPoint.getYPixel(lowY, highY, imgHeight);
          y2 += imageTopMargin;
          pixelMap.put(x2, y2);
          valueMap.put(x2, oldPoint.getY());
          g2d.setColor(imageLineColor.darker());
          g2d.drawLine(x1, y1, x2, y2);
          g2dh.setColor(imageLineColor);
          g2dh.drawLine(x1, y1, x2, y2);
        }
      }
      regularLine = temp;
      highlightedLine = tempHighlight;
    }
    if (!highlighted) {
      return regularLine;
    } else {
      return highlightedLine;
    }
  }

  @Override
  public final void componentResized(final ComponentEvent ce) {
    setBounds(ce.getComponent().getBounds());
  }

  @Override
  public void componentMoved(final ComponentEvent ce) {
  }

  @Override
  public void componentShown(final ComponentEvent ce) {
  }

  @Override
  public void componentHidden(final ComponentEvent ce) {
  }

  @Override
  public final void setMouseMotionLocation(final Point me) {
    oldMouseX = mouseX;
    mouseX = (int) me.getX();
    if (pixelMap.floorKey(mouseX) != null
        && pixelMap.ceilingKey(mouseX) != null) {
      int x1 = pixelMap.floorKey(mouseX);
      int y1 = pixelMap.get(x1).intValue();
      int x2 = pixelMap.ceilingKey(mouseX);
      int y2 = pixelMap.get(x2).intValue();
      if ((x2 - x1) > 0) {
        double m = ((double) y2 - y1) / (x2 - x1);
        double b = y1 - m * x1;
        mouseLocationCircle.x = mouseX - circleDiameter / 2;
        mouseLocationCircle.y = m * mouseX + b - circleDiameter / 2;
      } else if (pixelMap.containsKey(mouseX)) {
        mouseLocationCircle.x = mouseX - circleDiameter / 2;
        int y = pixelMap.get(mouseX) - circleDiameter / 2 - 2;
        mouseLocationCircle.y = y;
      }
    }
    if (mouseLocationCircle.contains(me)) {
      synchronized (highlightLock) {
        if (!highlighted) {
          highlighted = true;
        }
      }
    } else {
      synchronized (highlightLock) {
        if (highlighted) {
          highlighted = false;
        }
      }
    }
    value = "";
    if (mouseX >= leftMargin && mouseX <= getWidth() - rightMargin) {
      if (displayName) {
        value += name + ": ";
      }
      if (xPixelWeight != 0 && displayTime) {
        ZonedStockDateTime zsdt = startTime.getCopy();
        double xSeconds = (double) mouseX * xPixelWeight + xPixelWeight;
        zsdt.addSeconds((long) Math.ceil(xSeconds));
        value += zsdt.toString("yyyy/MM/dd HH:mm") + "   ";
      }
      if (displayValue && valueMap.floorKey(mouseX) != null) {
        int floorkey = valueMap.floorKey(mouseX);
        value += formatter.format(valueMap.get(floorkey));
      }
    }
    repaint();
  }
  @Override
  public final void setMouseClickLocation(final Point me) {
    
  }
}
