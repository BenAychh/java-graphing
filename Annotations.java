/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author benhernandez
 */
public class Annotations extends JPanel implements Graphable, MouseMotionListener, MouseListener{
  ArrayList<Note> notes;
  /**
   * The current image start time.
   */
  private ZonedStockDateTime startTime;  
  /**
   * The value corresponding to the last pixel of our window.
   */
  private ZonedStockDateTime endTime;
  /**
   * The value corresponding to the bottom pixel of our window.
   */
  private double lowY;
  /**
   * The value corresponding to the top pixel of our window.
   */
  private double highY;
  /**
   * The top margin.
   */
  private double topMargin = 0;
  /**
   * The bottom margin.
   */
  private double bottomMargin = 0;
  /**
   * The left margin.
   */
  private double leftMargin;
  /**
   * The right margin.
   */
  private double rightMargin;
  
  public Annotations() {
    addMouseListener(this);
    addMouseMotionListener(this);
    setOpaque(false);
    notes = new ArrayList<>();
    ZonedStockDateTime noteTime =
        new ZonedStockDateTime(LocalDateTime.of(2016, 2, 16, 12, 0));
    double value = 80;
    String message = "This is a test of the notes and the ability to round"
        + " long sentences and other text.";
    Note note = new Note(noteTime, value, 200, 100, "AAPL");
    note.setMessage(message);
    notes.add(note);
  }
  
  @Override
  public final void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    double xPixelWeight = startTime.getMarketSecondsUntil(endTime)
        / (getWidth() - rightMargin - leftMargin);
    double yPixelHeight = (getHeight() - topMargin - bottomMargin);
    for (Note note : notes) {
      note.setDimensions(startTime, xPixelWeight, lowY, highY, (int) yPixelHeight, leftMargin, topMargin);
      note.draw(g2d);     
    }
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
  public void setTextLocation(int x, int y, boolean fromBottom, boolean fromLeft) {
    
  }

  @Override
  public void setMargins(int top, int bottom, int left, int right) {
    this.topMargin = top;
    this.bottomMargin = bottom;
    this.leftMargin = left;
    this.rightMargin = right;
  }

  @Override
  public void setMouseMotionLocation(Point p) {
    
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    Point point = e.getPoint();
    for (Note note : notes) {
      note.move(point);
    }
    revalidate();
    repaint();
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    boolean repaint = false;
    for (Note note : notes) {
      if (note.mouseClick(e.getPoint())) {
        repaint = true;
      };
    }
    if (repaint) {
      repaint();
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    for (Note note : notes) {
      note.setMoveStart(e.getPoint());
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    for (Note note : notes) {
      note.stopMove();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    
  }

  @Override
  public void mouseExited(MouseEvent e) {
    
  }

  @Override
  public void setMouseClickLocation(Point p) {
    
  }
}
