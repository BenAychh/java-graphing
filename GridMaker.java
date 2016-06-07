/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JComponent;

/**
 *
 * @author Corporate
 */
public class GridMaker extends JComponent implements ComponentListener,
    Graphable {
  /**
   * The graph title.
   */
  private String title = "";
  /**
   * the image title.
   */
  private String imageTitle = "";
  /**
   * The top margin.
   */
  private int topMargin;
  /**
   * The bottom margin.
   */
  private int bottomMargin;
  /**
   * The left margin.
   */
  private int leftMargin;
  /**
   * The right margin.
   */
  private int rightMargin;
  /**
   * Whether or not to paint a mouse-following detailed x-value.
   */
  private boolean paintXValueOnScale;
  /**
   * The string which contains our exact X-value from the mouse.
   */
  private String exactXValue;
  /**
   * The location of the exact value of the x-axis.
   */
  private int exactX;
  /**
   * The xpixelweight (seconds per pixel).
   */
  private double xPixelWeight;
  /**
   * The way to format the x-axis.
   */
  private DateTimeFormatter dtf;
  /**
   * The mouse x location.
   */
  private int mousex;
  /**
   * If this is false then it means that our grid box width and heights are
   * given to us in seconds and values, not pixels. I.e. if true then
   * gridboxwidth = 100 means paint a grid box every 100 pixels, if false then
   * gridboxwidth = 100 means paint a grid box every 100 seconds;
   */
  private final boolean pixels;
  /**
   * How to format the y-axis scale.
   */
  private DecimalFormat yFormatter;
  /**
   * What to append at the end of the string.
   */
  private String appender;
  /**
   * The buffer from the bottom to display the text.
   */
  private static final int TEXTAXESBUFFER = 10;
  /**
   * Whether or not to draw the x axis.
   */
  private boolean drawXAxis;
  /**
   * Whether or not to draw a hard 0;
   */
  private boolean hardZero = false;
  /**
   * How far up from the bottom the xaxis is.
   */
  private int xAxisHeight;
  /**
   * How far up from the bottom the xaxis is.
   */
  private int imageXAxisHeight;
  /**
   * The width of our boxes. Also sets the frequency of information displayed on
   * our x-axis.
   */
  private int gridBoxWidth;
  /**
   * The width of our boxes. Also sets the frequency of information displayed on
   * our x-axis.
   */
  private int imageGridBoxWidth;
  /**
   * The height of our boxes. Also controls the frequency of information on the
   * y-axis.
   */
  private double gridBoxHeight;
  /**
   * The height of our boxes. Also controls the frequency of information on the
   * y-axis.
   */
  private double imageGridBoxHeight;
  /**
   * How far from the left the y-axis is.
   */
  private int yAxisWidth;
  /**
   * How far from the left the y-axis is.
   */
  private int imageYAxisWidth;
  /**
   * Whether or not to draw the x axis.
   */
  private boolean imageDrawXAxis;
  /**
   * Whether or not to draw the y axis.
   */
  private boolean drawYAxis;
  /**
   * Whether or not to draw the y axis.
   */
  private boolean imageDrawYAxis;
  /**
   * The stroke for the gridlines.
   */
  private BasicStroke gridlineStroke;
  /**
   * The stroke for the gridlines.
   */
  private BasicStroke imageGridlineStroke;
  /**
   * The stroke for the axes.
   */
  private BasicStroke axesStroke;
  /**
   * The stroke for the axes.
   */
  private BasicStroke imageAxesStroke;
  /**
   * The color to draw the grid lines.
   */
  private Color lineColor;
  /**
   * The color to draw the grid lines.
   */
  private Color imageLineColor;
  /**
   * The color to draw the Axes.
   */
  private Color axesColor;
  /**
   * The color to draw the Axes.
   */
  private Color imageAxesColor;
  /**
   * The color to draw the background.
   */
  private Color backgroundColor;
  /**
   * The color to draw the background.
   */
  private Color imageBackgroundColor;
  /**
   * The value corresponding to the 0th pixel of our window.
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
   * The image with the gridlines in it.
   */
  private BufferedImage gridLines;
  /**
   * Our fontsize.
   */
  private final int fontSize = 20;
  /**
   * Our mouseindicator color.
   */
  private final Color mouseIndicatorColor;
  /**
   * Sets a basic grid, black background, gray lines. dashed for the grid and
   * thickness 3 for the axes. Draws the x and y axes. Grid is a 50x50 square.
   */
  public GridMaker() {
    topMargin = 0;
    bottomMargin = 0;
    rightMargin = 0;
    leftMargin = 0;
    mouseIndicatorColor = Color.yellow;
    exactXValue = "";
    dtf = DateTimeFormatter.ofPattern("h:mm");
    xPixelWeight = 0;
    paintXValueOnScale = false;
    pixels = false;
    yFormatter = new DecimalFormat("$###,###,###.00");
    appender = "";
    imageStartTime = new ZonedStockDateTime(LocalDateTime.now());
    imageEndTime = new ZonedStockDateTime(LocalDateTime.now());
    startTime = new ZonedStockDateTime(LocalDateTime.now());
    endTime = new ZonedStockDateTime(LocalDateTime.now());
    final int defaultTo15Minutes = 15 * 60;
    gridBoxWidth = defaultTo15Minutes;
    gridBoxHeight = 1;
    backgroundColor = Color.black;
    axesColor = Color.white;
    lineColor = Color.darkGray;
    final int dashLength = 5;
    final float totalGroupLength = 10.0f;
    gridlineStroke = new BasicStroke(1.0f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER,
        totalGroupLength, new float[]{dashLength}, 0.0f);
    final int axesStrokeWidth = 3;
    axesStroke = new BasicStroke(axesStrokeWidth);
    drawXAxis = true;
    drawYAxis = true;
  }

  /**
   * Draws the grid over the whole background.
   *
   * @param grphcs **the Graphics we draw on. Duh**
   */
  @Override
  protected final void paintComponent(final Graphics grphcs) {
    Graphics2D g2d = (Graphics2D) grphcs;
    g2d.drawImage(gridImage(), null, 0, 0);
    g2d.setFont(new Font("Calibri", Font.BOLD, fontSize));
    FontMetrics fm = g2d.getFontMetrics();
    if (paintXValueOnScale && exactXValue.length() != 0) {
      int fontWidth = fm.stringWidth(exactXValue);
      int y = getHeight() - imageXAxisHeight + TEXTAXESBUFFER + 2;
      g2d.setColor(mouseIndicatorColor);
      final int textBufferSize = 10;
      g2d.fillRect((int) (exactX - fontWidth / 2 - textBufferSize / 2),
          0, fontWidth + textBufferSize, fm.getHeight());
      g2d.setColor(backgroundColor);
      g2d.drawString(exactXValue, exactX - fontWidth / 2,
          y + fm.getDescent());
    }
  }

  /**
   * public setter for drawing the x-axis.
   *
   * @param draw **true (draw) or false (don't draw)**
   */
  public final void setDrawXAxis(final boolean draw) {
    drawXAxis = draw;
  }

  /**
   * public setter for the x-axis height. How far above the bottom is.
   *
   * @param height **the # of pixels above the bottom to draw the x-axis**
   */
  public final void setXAxisHeight(final int height) {
    xAxisHeight = height;
  }

  /**
   * public setter for drawing the y-axis.
   *
   * @param draw **true (draw) or false (don't draw)**
   */
  public final void setDrawYAxis(final boolean draw) {
    drawYAxis = draw;
  }

  /**
   * public setter for the y-axis width. How far it sticks out from the left.
   *
   * @param width **the # of pixels to stick out from the left**
   */
  public final void setYAxisWidth(final int width) {
    yAxisWidth = width;
  }

  /**
   * Sets the width of the boxes made by our grid.
   *
   * @param width **the width of the box on our grid**
   */
  public final void setGridBoxWidth(final int width) {
    gridBoxWidth = width;
  }

  /**
   * Sets the title of our graph.
   * @param pTitle What to name our graph.
   */
  public final void setTitle(final String pTitle) {
    title = pTitle;
  }

  /**
   * Sets the height of our boxes made by our grid.
   *
   * @param height **the height of our grid boxes**
   */
  public final void setGridBoxHeight(final double height) {
    gridBoxHeight = height;
  }

  /**
   * public setter for the gridline stroke.
   *
   * @param bs **BasicStroke to draw the gridlines**
   */
  public final void setGridlineStroke(final BasicStroke bs) {
    gridlineStroke = bs;
  }

  /**
   * public setter for the axes stroke.
   *
   * @param bs **BasicStroke to draw the axes**
   */
  public final void setAxesStroke(final BasicStroke bs) {
    axesStroke = bs;
  }

  /**
   * sets the grid line color.
   *
   * @param color **the color of the grid lines**
   */
  public final void setGridlineColor(final Color color) {
    lineColor = color;
  }

  /**
   * sets the axes color.
   *
   * @param color **the color of the axes**
   */
  public final void setAxesColor(final Color color) {
    axesColor = color;
  }

  /**
   * sets the background color.
   *
   * @param color **the color of the background**
   */
  public final void setBackgroundColor(final Color color) {
      backgroundColor = color;
  }

  public final void setTimes (final ZonedStockDateTime start,
      final ZonedStockDateTime end) {
    this.startTime = start;
    this.endTime = end;
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
   * Gets the low Y value.
   *
   * @return this.lowY.
   */
  public final double getLowY() {
    return this.lowY;
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
   * Gets the high Y value.
   *
   * @return this.highY.
   */
  public final double getHighY() {
    return this.highY;
  }
  /**
   * Sets the format string used to for the y-axis.
   *
   * @param s How to format the numbers.
   */
  public final void setYFormatter(final String s) {
    yFormatter = new DecimalFormat(s);
    appender = "";
  }

  /**
   * Sets the format string used for the y-axis, also adds an appender.
   *
   * @param s How to format the numbers.
   * @param pAppender What to append at the end of the string.
   */
  public final void setYFormatter(final String s, final String pAppender) {
    yFormatter = new DecimalFormat(s);
    appender = pAppender;
  }
  /**
   * Determines if we want to draw the xValues on our scale.
   * @param draw true or false.
   */
  public final void setDrawExactX(final boolean draw) {
    paintXValueOnScale = draw;
  }

  /**
   * Sets if we should draw the hard zero or not.
   * @param on true if draw the hard zero.
   */
  public final void setHardZero(final boolean on) {
    this.hardZero = on;
  }

  /**
   * Sets the margins of our grapher.
   * @param top the top margin.
   * @param bottom the bottom margin.
   * @param left the left margin.
   * @param right the right margin.
   */
  @Override
  public final void setMargins(final int top, final int bottom,
      final int left, final int right) {
    topMargin = top;
    bottomMargin = bottom;
    leftMargin = left;
    rightMargin = right;
  }

  /**
   * Sets the values if we are given any new information.
   */
  private void setValues() {
    imageTitle = title;
    imageDrawXAxis = drawXAxis;
    imageXAxisHeight = xAxisHeight;
    imageDrawYAxis = drawYAxis;
    imageYAxisWidth = yAxisWidth;
    imageGridBoxHeight = gridBoxHeight;
    imageGridlineStroke = gridlineStroke;
    imageAxesStroke = axesStroke;
    imageLineColor = lineColor;
    imageAxesColor = axesColor;
    imageBackgroundColor = backgroundColor;
    imageStartTime = startTime.getCopy();
    imageEndTime = endTime.getCopy();
    long marketSpanInSeconds
        = imageStartTime.getMarketSecondsUntil(imageEndTime);
    if (marketSpanInSeconds <= 3600) {
      gridBoxWidth = 5 * 60;
    } else if (marketSpanInSeconds < 23400) {
      gridBoxWidth = 15 * 60;
    } else if (marketSpanInSeconds < 23400 * 3) {
      gridBoxWidth = 30 * 60;
    } else if (marketSpanInSeconds < 23400 * 5) {
      gridBoxWidth = 60 * 60;
    } else {
      gridBoxWidth = gridBoxWidth;
    }
    imageGridBoxWidth = gridBoxWidth;
    imageLowY = lowY;
    imageHighY = highY;
  }

  /**
   * Checks to see if any data has changed since we last drew our image.
   *
   * @return true if the image needs to be redrawn.
   */
  private synchronized boolean isChanged() {
    return (!imageTitle.equals(title)
        || imageDrawXAxis != drawXAxis
        || imageXAxisHeight != xAxisHeight
        || imageDrawYAxis != drawYAxis
        || imageYAxisWidth != yAxisWidth
        || imageGridBoxWidth != gridBoxWidth
        || imageGridBoxHeight != gridBoxHeight
        || imageGridlineStroke != gridlineStroke
        || imageAxesStroke != axesStroke
        || imageLineColor != lineColor
        || imageAxesColor != axesColor
        || imageBackgroundColor != backgroundColor
        || !imageStartTime.equals(startTime)
        || !imageEndTime.equals(endTime)
        || imageLowY != lowY
        || imageHighY != highY
        || gridLines.getWidth() != getWidth()
        || gridLines.getHeight() != getHeight());
  }

  /**
   * Does all of the calculations for our image.
   *
   * @return a BufferedImage with the gridlines drawn.
   */
  public final synchronized  BufferedImage gridImage() {
    if (isChanged() && startTime != null && endTime != null) {
      setValues();
      BufferedImage temp = new BufferedImage(getWidth(), getHeight(),
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = (Graphics2D) temp.getGraphics();
      g2d.setColor(imageBackgroundColor);
      g2d.fillRect(0, 0, getWidth(), getHeight());
      if (!imageTitle.isEmpty()) {
        AffineTransform saveXform = g2d.getTransform();
        Font g2dFont = g2d.getFont();
        g2d.setColor(axesColor);
        final int titleFontSize = 25;
        g2d.setFont(new Font("Calibri", Font.BOLD, titleFontSize));
        final int degreesToRotate = 90;
        g2d.rotate(Math.toRadians(degreesToRotate));
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(imageTitle);
        g2d.drawString(imageTitle, getHeight() / 2 - width / 2, -1 * getWidth()
            + titleFontSize);
        g2d.setFont(g2dFont);
        g2d.setTransform(saveXform);
      }
      int lineHeight = getHeight() - topMargin - bottomMargin;
      if (drawXAxis) {
        lineHeight -= imageXAxisHeight;
      }

      int lineWidth = getWidth() - rightMargin
          - leftMargin;
      if (drawYAxis) {
        lineWidth -= imageYAxisWidth;
      }
      g2d.setStroke(imageGridlineStroke);
      g2d.setColor(imageLineColor);

      double tempGridWidth = imageGridBoxWidth;
      xPixelWeight
          = (double) imageStartTime.getMarketSecondsUntil(imageEndTime)
          / (double) lineWidth;
      if (!pixels) {
        tempGridWidth = ((double) imageGridBoxWidth / xPixelWeight);
      }
      for (int i = 1; i <= lineWidth / tempGridWidth; i++) {
        double x = tempGridWidth * (double) i;
        g2d.drawLine((int) x, topMargin, (int) x, lineHeight + topMargin);
      }
      double tempGridHeight = imageGridBoxHeight;
      double yPixelWeight = (imageHighY - imageLowY) / lineHeight;
      if (!pixels) {
        tempGridHeight = ((double) imageGridBoxHeight / yPixelWeight);
      }
      for (int i = 0; i <= lineHeight / tempGridHeight + 1; i++) {
        double y = lineHeight + topMargin - tempGridHeight * (double) i;
        g2d.drawLine(0 + leftMargin, (int) y, lineWidth, (int) y);
      }
      if (this.hardZero) {
        int y = (int) (lineHeight + topMargin + (imageLowY / yPixelWeight));
        g2d.setColor(axesColor);
        g2d.setStroke(axesStroke);
        g2d.drawLine(0 + leftMargin, y, lineWidth, y);
      }
      ZonedStockDateTimeIntervals zsdti = new ZonedStockDateTimeIntervals();
      zsdti.setIntervalEndPoints(imageStartTime, imageEndTime);
      zsdti.next();
      long totalSeconds = imageStartTime.getMarketSecondsUntil(imageEndTime);
      while (zsdti.next()) {
        long tempSeconds
            = imageStartTime.getMarketSecondsUntil(zsdti.getStart());
        double ratio = (double) tempSeconds / (double) totalSeconds;
        g2d.setStroke(imageGridlineStroke);
        g2d.setColor(imageAxesColor);
        double x = lineWidth * ratio;
        g2d.drawLine((int) x, topMargin, (int) x, lineHeight + topMargin);
      }
      if (imageDrawYAxis) {
        g2d.setColor(imageAxesColor);
        g2d.setStroke(imageAxesStroke);
        g2d.drawLine(lineWidth, 0,
            lineWidth, topMargin + lineHeight
            + bottomMargin + TEXTAXESBUFFER);
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 0; i <= lineHeight / tempGridHeight + 1; i++) {
          double y = lineHeight + topMargin - tempGridHeight
              * (double) i;
          double yValue = (lineHeight + topMargin - y)
              * yPixelWeight + imageLowY;
          g2d.drawString(yFormatter.format(yValue) + appender, lineWidth
              + TEXTAXESBUFFER, (int) y
              + fm.getDescent());
        }


//        if (this.hardZero) {
//          int y = (int) (lineHeight + topMargin + (imageLowY / yPixelWeight));
//          g2d.drawString("0", lineWidth + TEXTAXESBUFFER, y +
//              fm.getDescent());
//        }

      }
      if (imageDrawXAxis) {
        g2d.setColor(axesColor);
        g2d.setStroke(axesStroke);
        g2d.drawLine(0,
            getHeight() - topMargin - bottomMargin - xAxisHeight,
            lineWidth,
            getHeight() - topMargin - bottomMargin - xAxisHeight);
        ZonedStockDateTime zsdt = imageStartTime.getCopy();
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 1; i <= lineWidth / tempGridWidth; i++) {
          double x = leftMargin + tempGridWidth * (double) i;
          zsdt.addSeconds((long) (tempGridWidth * xPixelWeight) + 1);
          int y = getHeight() - bottomMargin - imageXAxisHeight
              + TEXTAXESBUFFER;
          int fontWidth = fm.stringWidth(zsdt.toString(dtf));
          g2d.drawString(zsdt.toString(dtf), (int) x - fontWidth / 2,
              y + fm.getDescent());
        }
      }
      gridLines = temp;
    }
    return gridLines;
  }

  @Override
  public final void componentResized(final ComponentEvent ce) {
    setBounds(ce.getComponent().getBounds());
  }

  @Override
  public final void componentMoved(final ComponentEvent ce) {
    
  }

  @Override
  public final void componentShown(final ComponentEvent ce) {

  }

  @Override
  public final void componentHidden(final ComponentEvent ce) {
  }

  @Override
  public final void setMouseMotionLocation(Point me) {
    mousex = (int) me.getX();
    if (xPixelWeight != 0 && paintXValueOnScale) {
      double tempGridWidth = imageGridBoxWidth;
      if (!pixels) {
        tempGridWidth = (imageGridBoxWidth / xPixelWeight);
      }
      ZonedStockDateTime zsdt = imageStartTime.getCopy();
      double xSeconds = mousex * xPixelWeight + xPixelWeight;
      zsdt.addSeconds((long) Math.ceil(xSeconds));
      if (zsdt.isBefore(imageStartTime)) {
        exactXValue = imageStartTime.toString(dtf);
      } else if (zsdt.isAfter(imageEndTime)) {
        exactXValue = imageEndTime.toString(dtf);
      } else {
        exactXValue = zsdt.toString(dtf);
      }
      exactX = Math.max(Math.min(mousex, getWidth() - rightMargin),
          leftMargin);
      repaint();
    }
  }
  
  @Override
  public final void setMouseClickLocation(Point me) {
    
  }

  @Override
  public void setTextLocation(int x, int y, boolean fromBottom, boolean fromLeft) {
    //Unneeded.
  }
}
