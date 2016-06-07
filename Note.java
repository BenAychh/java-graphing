/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 *
 * @author benhernandez
 */
public class Note {
  private ZonedStockDateTime oldTime;
  private ZonedStockDateTime xTime;
  private double oldValue;
  private double yValue;
  private String message;
  private int width;
  private int height;
  private String symbol;
  private final Color color;
  private final int triangleHeight = 20;
  private final int triangleWidth = 30;
  private final int draggableWidth = 16;
  private final int closeableWidth = 10;
  private Point mouseStart;
  private ZonedStockDateTime start;
  private double xPixelWeight;
  private double bottom;
  private double top;
  private int yPixelHeight;
  private double leftMargin;
  private double topMargin;
  private Rectangle thisRectangle;
  private Rectangle sizeRectangle;
  private Rectangle closeRectangle;
  private Polygon triangle;
  private boolean moving = false;
  private boolean resizing = false;
  private boolean up = true;
  public Note(final ZonedStockDateTime pTime, final double pValue,
      final int pWidth, final int pHeight, final String pSymbol) {
    oldTime = pTime;
    xTime = pTime;
    oldValue = pValue;
    yValue = pValue;
    symbol = pSymbol;
    width = pWidth;
    color = new Color(255, 0, 255, 100);
    mouseStart = null;
    thisRectangle = new Rectangle();
    sizeRectangle = new Rectangle();
    closeRectangle = new Rectangle();
    triangle = new Polygon();
  }
  public final void setMessage(String pMessage) {
    message = pMessage;
  }
  public final void setDimensions(final ZonedStockDateTime pStart,
      final double pXPixelWeight, final double pBottom, final double pTop,
      final int pYPixelHeight, final double pLeftMargin, final double pTopMargin) {
    start = pStart;
    xPixelWeight = pXPixelWeight;
    bottom = pBottom;
    top = pTop;
    yPixelHeight = pYPixelHeight;
    leftMargin = pLeftMargin;
    topMargin = pTopMargin;
  }
  public final Rectangle getRectangle() {
    return thisRectangle;
  }
  public final void draw(final Graphics2D g2d) {
    final int margin = 2;
    height = getTextHeight(g2d, margin);
    int x = convertTimeToPixel();
    double valueHeight = top - bottom;
    int y = convertValueToPixel();
    drawTriangle(g2d, x, y);
    x -= width / 2;
    if (up) {
      y -= height + triangleHeight;
    } else {
      y += triangleHeight;
    }
    g2d.setColor(color);
    drawBody(g2d, x, y);
    drawResizer(g2d, x, y);
    drawClose(g2d, x, y);
    drawStringMultiLine(
        g2d, margin, x + margin, y + margin);
  }
  private void drawBody(Graphics2D g2d, int x, int y) {
    thisRectangle.setBounds(x, y, width, height);
    g2d.fillRect(x, y, width, height);
  }
  private void drawResizer(Graphics2D g2d, int x, int y) {
    int resizerY = y;
    if (!up) {
      resizerY += height;
    }
    g2d.fillRect(x + width - draggableWidth  / 2,
        resizerY - draggableWidth / 2, draggableWidth, draggableWidth);
    sizeRectangle.setBounds(x + width - draggableWidth / 2,
        resizerY - draggableWidth / 2, draggableWidth, draggableWidth);
  }
  private void drawTriangle(Graphics2D g2d, int x, int y) {
    g2d.setColor(color);
    int[] triangleXs = new int[3];
    triangleXs[0] = x;
    triangleXs[1] = x - triangleWidth / 2;
    triangleXs[2] = x + triangleWidth / 2;
    int[] triangleYs = new int[3];
    triangleYs[0] = y;
    if (up) {
      triangleYs[1] = y - triangleHeight;
    } else {
      triangleYs[1] = y + triangleHeight;
    }
    triangleYs[2] = triangleYs[1];
    triangle = new Polygon(triangleXs, triangleYs, 3);
    g2d.fillPolygon(triangleXs, triangleYs, 3);
  }
  private void drawClose(Graphics2D g2d, int x, int y) {
    int closerY = y;
    if (!up) {
      closerY += height;
    }
    int x1 = x - closeableWidth / 2;
    int y1 = closerY - closeableWidth / 2;
    g2d.setColor(color.black);
    g2d.fillRect(x1, y1, closeableWidth, closeableWidth);
    closeRectangle.setBounds(x1, y1, closeableWidth, closeableWidth);
    int x2 = x1 + closeableWidth;
    int y2 = y1 + closeableWidth;
    g2d.setColor(Color.white);
    g2d.drawRect(x1, y1, closeableWidth, closeableWidth);
    g2d.drawLine(x1, y1, x2, y2);
    g2d.drawLine(x1 + closeableWidth, y1, x2 - closeableWidth, y2);
  }
  public final boolean mouseClick(Point p) {
    System.out.println("click!");
    if (triangle.contains(p)) {
      this.up = !this.up;
      return true;
    } else if (closeRectangle.contains(p)) {
      System.out.println("close");
      return true;
    }
    return false;
  }
  public final boolean setMoveStart(Point p) {
    if (sizeRectangle.contains(p)) {
      mouseStart = p;
      resizing = true;
      System.out.println(resizing);
      return true;
    }
    if (thisRectangle.contains(p)) {
      mouseStart = p;
      moving = true;
      return true;
    } else {
      return false;
    }
  }
  public final void move(Point p) {
    if (mouseStart != null) {
      if (moving) {
        ZonedStockDateTime newTime = xTime.getCopy();
        double left = Math.min(p.getX(), mouseStart.getX());
        double right = Math.max(p.getX(), mouseStart.getX());
        long xMove = convertPixelToSeconds((int) (right - left));
        if (right == p.getX()) {
          newTime.addSeconds(xMove);
        } else {
          newTime.subtractSeconds(xMove);
        }
        xTime = newTime;
        double newValue = convertPixelToValue((int) (mouseStart.getY() - p.getY()));
        yValue += newValue;
        mouseStart = p;
      } else if (resizing) {
        width += p.getX() - mouseStart.getX();
        mouseStart = p;
      }
    }
  }
  public final void stopMove() {
    mouseStart = null;
    moving = false;
    resizing = false;
  }
  private int getTextHeight(Graphics2D g2d, int margin) {
    int textWidth = width - margin * 2;
    FontMetrics fm = g2d.getFontMetrics();
    height = fm.getHeight();
    if(fm.stringWidth(message) < textWidth) {
        return height;
    } else {
        String[] words = message.split(" ");
        String currentLine = words[0];
        for(int i = 1; i < words.length; i++) {
            if(fm.stringWidth(currentLine+words[i]) < textWidth) {
                currentLine += " "+words[i];
            } else {
                height += fm.getHeight();
                currentLine = words[i];
            }
        }
        if(currentLine.trim().length() > 0) {
            height += fm.getHeight();
        }
        return height;
    }
  }
  private void drawStringMultiLine(final Graphics2D g2d, final int margin,
      final int pX, final int pY) {
    int x = pX;
    int y = pY;
    int textWidth = width - margin * 2;
    g2d.setColor(Color.white);
    FontMetrics m = g2d.getFontMetrics();
    y += m.getHeight();
    if(m.stringWidth(message) < textWidth) {
        g2d.drawString(message, x, y);
    } else {
        String[] words = message.split(" ");
        String currentLine = words[0];
        for(int i = 1; i < words.length; i++) {
            if(m.stringWidth(currentLine+words[i]) < textWidth) {
                currentLine += " "+words[i];
            } else {
                g2d.drawString(currentLine, x, y);
                y += m.getHeight();
                currentLine = words[i];
            }
        }
        if(currentLine.trim().length() > 0) {
            g2d.drawString(currentLine, x, y);
        }
    }
  }
  private int convertTimeToPixel() {
     return (int) (leftMargin + (int)
         Math.round(start.getMarketSecondsUntil(xTime) / xPixelWeight));
  }
  private long convertPixelToSeconds(final int pixel) {
    return (long) (pixel * xPixelWeight);
  }
  private int convertValueToPixel() {
    double valueHeight = top - bottom;
    return (int) (topMargin + (int) Math.round(yPixelHeight
        - (yPixelHeight / valueHeight * (yValue - bottom))));
  }
  private double convertPixelToValue(final int pixel) {
    return 100 - (bottom - (pixel - yPixelHeight) * (top - bottom) / yPixelHeight);
  }
}
