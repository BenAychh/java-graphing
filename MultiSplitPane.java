package net.candhcapital.Graphing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Corporate
 */
public class MultiSplitPane extends JPanel implements ComponentListener,
    MouseListener, MouseMotionListener {

  /**
   * The array of things that want to know about the mouse.
   */
  private ArrayList<Graphable> mouseMotionListeners;
  private ArrayList<Graphable> mouseClickListeners;
  /**
   * HardCoded bottom panel.
   */
  private boolean bottomSetHeightTrue;
  /**
   * HardCoded bottom panel height.
   */
  private int bottomSetHeight;
  /**
   * Component for bottomSetHeight.
   */
  private JComponent bottomSetHeightComponent;
  /**
   * Separator height.
   */
  private static final int DIVIDERHEIGHT = 3;
  /**
   * The children of the pane (individual cells).
   */
  private final MultiSplitChildren children;
  /**
   * A list of the separators used.
   */
  private final LinkedList<MultiSeparator> separators;
  /**
   * Turns to true if the mouse is Pressed on a divider.
   */
  public boolean dragging = false;
  /**
   * Need to use mouseClick to determine where the mouse started.
   */
  private int mouseStartY;
  /**
   * So we know which pane the active divider is between.
   */
  private String topPane;
  /**
   * So we know which pane the active divider is between.
   */
  private String bottomPane;
  /**
   * The sum of the heights of our two active panes for calculations.
   */
  private double sumHeights;
  /**
   * The sum of the weights of our two active panes for calculations.
   */
  private double sumWeights;
  /**
   * Need to store the original top height so we can calculate new weights.
   */
  private double topHeight;
  /**
   * Storing the mouse location for anything else that wants to use it.
   */

  /**
   * Sets the layout to null for the Panel.
   */
  public MultiSplitPane() {
    addMouseListener(this);
    addMouseMotionListener(this);
    mouseMotionListeners = new ArrayList<>();
    mouseClickListeners = new ArrayList<>();
    bottomSetHeightTrue = false;
    bottomSetHeight = 0;
    bottomSetHeightComponent = new JComponent() {
    };
    children = new MultiSplitChildren();
    separators = new LinkedList<>();
    setLayout(null);
  }

  /**
   * Adds a set of MultiSplitChildren, does all adding before recalculating.
   * This method is meant to make it easy to initially add a bunch of panels
   * without having to worry about the exponential shrinking.
   *
   * @param theKids the array of kids to add.
   */
  public final void addChildren(final MultiSplitChild[] theKids) {
    children.clear();
    double kidWeights = 0;
    for (MultiSplitChild kid : theKids) {
      kidWeights += kid.getWeight();
    }
    // Alleviates base2 errors.
    final double equalMin = 0.999999999;
    final double equalMax = 1.000000001;
    if (kidWeights > equalMin && kidWeights < equalMax) {
      for (MultiSplitChild kid : theKids) {
        children.add(kid);
      }
    } else {
      throw new Error("The weights of all panels must sum to 1 exactly. ("
          + kidWeights + ")");
    }
    recalculate();
  }
  /**
   * Removes everything and displays nothing.
   */
  public final void removeAllChildren() {
    children.clear();
  }
  /**
   * Adds individual children to the pane. If it is the first child and it's
   * weight is not 1, it will automatically correct it to 1.
   *
   * @param name the name of the child.
   * @param component the component to display.
   * @param weight the weight to make this component take up.
   */
  public final void addChild(final String name, final JComponent component,
      final double weight) {
    double tempWeight = weight;
    if (children.size() == 0) {
      tempWeight = 1;
    }
    children.add(name, component, tempWeight);
    recalculate();
  }

  /**
   * Passes mouse information on to this child.
   * @param graphable
   */
  public final void addMouseMotionChild(final Graphable graphable) {
    mouseMotionListeners.add(graphable);
  }
  /**
   * Passes mouse information on to this child.
   * @param graphable
   */
  public final void addMouseClickChild(final Graphable graphable) {
    mouseClickListeners.add(graphable);
  }

  /**
   * Removes a child from the panel. more calculation.
   *
   * @param name the name of the child to remove.
   */
  public final void removeChild(final String name) {
    children.remove(name);
    recalculate();
  }

  /**
   * Redraws the window with the components laid out manually.
   */
  private void recalculate() {
    removeAll();
    separators.clear();
    int start = 0;
    if (!children.isEmpty()) {
      int dividersCount = children.size() - 1;
      for (MultiSplitChild child : children.values()) {
        double weight = child.getWeight();
        int height = (int) Math.floor(weight * (getHeight()
            - DIVIDERHEIGHT * dividersCount - bottomSetHeight));
        JComponent c = child.getComponent();
        this.add(c);
        Rectangle r = new Rectangle(0, start, getWidth(), height);
        c.setBounds(r);
        if (start == 0) {
          start += height + DIVIDERHEIGHT + 1;
        } else {
          separators.get(separators.size() - 1)
              .setBottomName(child.getName());
          start += height + DIVIDERHEIGHT;
        }
        MultiSeparator separator = new MultiSeparator();
        separators.add(separator);
        separator.setTopName(child.getName());
        Rectangle q = new Rectangle(0, start + 1, getWidth(),
            DIVIDERHEIGHT);
        separator.setBounds(q);
        add(separator);
      }
      remove(separators.get(separators.size() - 1));
      if (bottomSetHeightTrue) {
        add(bottomSetHeightComponent);
        Rectangle r
            = new Rectangle(0, start, getWidth(), bottomSetHeight);
        bottomSetHeightComponent.setBounds(r);
      }
      revalidate();
      repaint();
    }
  }

  /**
   * Turns on the bottom Jpanel (in case we want something permanently at the
   * bottom).
   * @param height how high our permanent panel should be.
   * @param component what our bottom component should be.
   */
  public final void setBottomOn(final int height, final JComponent component) {
    bottomSetHeightTrue = true;
    bottomSetHeight = height;
    bottomSetHeightComponent = component;
  }

  public final void setBottomOff() {
    bottomSetHeight = 0;
    bottomSetHeightTrue = false;
  }

  /**
   * Need to manually redraw when we are resized.
   *
   * @param ce the component event.
   */
  @Override
  public final void componentResized(final ComponentEvent ce) {
    recalculate();
  }

  /**
   * Need to manually redraw when we are moved to a difference container.
   *
   * @param ce the component event.
   */
  @Override
  public final void componentMoved(final ComponentEvent ce) {
    recalculate();
  }

  /**
   * What to do when the component is shown.
   *
   * @param ce the component event.
   */
  @Override
  public final void componentShown(final ComponentEvent ce) {
    //recalculate();
  }

  /**
   * What to do when the component is hidden.
   *
   * @param ce the component event.
   */
  @Override
  public final void componentHidden(final ComponentEvent ce) {
  }

  /**
   * Mouse click event.
   *
   * @param me mouse click.
   */
  @Override
  public void mouseClicked(final MouseEvent me) {
    for (Graphable ml : mouseClickListeners) {
      Point componentPoint = SwingUtilities
          .convertPoint(this, me.getPoint(), (Component) ml);
      ml.setMouseClickLocation(componentPoint);
    }
    repaint();
  }

  /**
   * When the mouse is pressed. It checks to see if it is on a separator. If it
   * is, then it makes not of the location and the panels it is between.
   *
   * @param me the mouse event.
   */
  @Override
  public final void mousePressed(final MouseEvent me) {
    for (MultiSeparator ms : separators) {
      if (ms.checkMouseY(me.getY())) {
        dragging = true;
        topPane = ms.getTopName();
        bottomPane = ms.getBottomName();
        if (!bottomPane.equals("")) {
          sumHeights = children.getChildHeight(topPane)
              + children.getChildHeight(bottomPane);
          sumWeights = children.getChildWeight(topPane)
              + children.getChildWeight(bottomPane);
          topHeight = children.getChildHeight(topPane);
          mouseStartY = me.getY();
        }
        break;
      }
    }
    extendableMousePressed(me);
  }
  /**
   * For anyone who wants to do something with the mouse presses.
   * @param me the mouse event.
   */
  public void extendableMousePressed(final MouseEvent me) {
  }

  /**
   * Stop the moving of the panes when we let go.
   *
   * @param me the mouse event.
   */
  @Override
  public final void mouseReleased(final MouseEvent me) {
    dragging = false;
    extendableMouseReleased(me);
  }
  /**
   * For anyone who wants to do something with the releases.
   * @param me the mouse event.
   */
  public void extendableMouseReleased(final MouseEvent me) {

  }

  /**
   * Mouse enter event.
   *
   * @param me mouse enter.
   */
  @Override
  public void mouseEntered(final MouseEvent me) {
  }

  /**
   * Mouse exited event.
   *
   * @param me mouse exited.
   */
  @Override
  public void mouseExited(final MouseEvent me) {
  }

  /**
   * When we started Press on a divider then move the panes around.
   *
   * @param me mouse event.
   */
  @Override
  public final void mouseDragged(final MouseEvent me) {
    if (dragging && !bottomPane.equals("")) {
      int mouseDifference = me.getY() - mouseStartY;
      double newTopHeight = Math.max(Math.min(sumHeights, topHeight
          + mouseDifference), 0);
      double newWeight = newTopHeight / sumHeights * sumWeights;
      children.setChildWeight(topPane, newWeight);
      children.setChildWeight(bottomPane, sumWeights - newWeight);
      recalculate();
    }
    extendableMouseDragged(me);
  }

  /**
   * For anyone who wants to extend mouse dragging.
   * @param me the mouse event.
   */
  public void extendableMouseDragged(final MouseEvent me) {
  }

  /**
   * Mouse moved.
   *
   * @param me mouse event.
   */
  @Override
  public final synchronized void mouseMoved(final MouseEvent me) {
    for (Graphable ml : mouseMotionListeners) {
      Point componentPoint = SwingUtilities
          .convertPoint(this, me.getPoint(), (Component) ml);
      ml.setMouseMotionLocation(componentPoint);
    }
    repaint();
  }

  /**
   * For anyone who wants to extend the mouse movement.
   * @param me the mouse event.
   */
  public void extendableMouseMoved(final MouseEvent me) {
  }
}
