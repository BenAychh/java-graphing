package net.candhcapital.Graphing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



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
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 *
 * @author Corporate
 */
public class BlockGraph extends JComponent implements ComponentListener,
        Graphable {
    /**
     * Formatter for our value display
     */
    private DecimalFormat formatter;
    /**
     * The name of our graph;
     */
    private String name;
    /**
     * The current mouse location.
     */
    int mouseX;
    /**
     * Y-Location to draw Value Text.
     */
    private int textY = 0;
    /**
     * X-Location to draw Value Text.
     */
    private int textX = 0;
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
     * the width in seconds of our candles.
     */
    private int candleWidth = 0;
    /**
     * The Array of the blocks.
     */
    private ArrayList<Block> blocks;
    /**
     * The image of our volumes.
     */
    private BufferedImage blocksImage;

    /**
     * Initializer.
     */
    public BlockGraph() {
        name = "";
        mouseX = -1;
        formatter = new DecimalFormat("#,###");
        blocksImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        blocks = new ArrayList<>();
    }
    /**
     * Calls drawBlocks so it can paint the bufferedImage.
     * @param g **the graphics**
     */
    @Override
    protected final void paintComponent(final Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(drawBlocks(), null, 0, 0);
        if (mouseX >= leftMargin && mouseX <= getWidth() - rightMargin) {
            for (Block vb : blocks) {
                if (vb.matchMouseX(mouseX)) {
                    g.setFont(new Font("Calibri", Font.PLAIN, 16));
                    RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHints(rh);
                    g2d.setColor(Color.white);
                    String string = name + ": "
                              + formatter.format(vb.getValue());
                    if (name.isEmpty()) {
                      string = formatter.format(vb.getValue());
                    } 
                    g2d.drawString(string,
                            (int) (textX + imageLeftMargin),
                            (int) (getHeight() - textY - imageBottomMargin
                            - g2d.getFontMetrics().getHeight()));
                    Color barInterior;
                    Color barBorder = vb.getColor();
                    int brightener = 2;
                    if (vb.isRed()) {
                        brightener = 4;
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
                    g2d.fillRect(vb.getX(), vb.getY(), vb.getWidth(),
                            vb.getHeight());
                }
            }
        }
    }
    private BufferedImage drawBlocks() {
        if (isChanged() && startTime != null && endTime != null) {
            setValues();
            BufferedImage temp = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = temp.createGraphics();
            RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHints(rh);
            g2d.setStroke(new BasicStroke(2));
            double xPixelValue =
                    (double) (imageStartTime.getSecondsBetween(imageEndTime))
                    / (temp.getWidth() - imageLeftMargin - imageRightMargin);
            int imgHeight = (int) (temp.getHeight() - imageTopMargin
                    - imageBottomMargin);
            for (Block vb : blocks) {
                vb.setRectangles(startTime, xPixelValue, candleWidth, lowY,
                        highY, imgHeight, topMargin);
                g2d.setColor(vb.getColor());
                g2d.drawRect(vb.getX(), vb.getY(), vb.getWidth(),
                        vb.getHeight());
                Color barInterior = new Color(vb.getColor().getRed(),
                        vb.getColor().getGreen(), vb.getColor().getBlue(), 100);
                g2d.setColor(barInterior);
                g2d.fillRect(vb.getX(), vb.getY(), vb.getWidth(),
                        vb.getHeight());
            }
            blocksImage = temp;
        }
        return blocksImage;
    }
    /**
     * Sets the margins for our window.
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public final void setMargins(int top, int bottom, int left, int right) {
        topMargin = top;
        bottomMargin = bottom;
        leftMargin = left;
        rightMargin = right;
    }
    /**
     * Sets the time corresponding to the 0th pixel.
     * @param zsdt **the beginning time of our window**
     */
    public final void setTimeStart(final ZonedStockDateTime zsdt) {
        this.startTime = zsdt;
    }
    /**
     * Sets the width of our candles.
     * @param pBlocks The blocks from our database.
     * @param pCandleWidth The width of our candles.
     */
    public final void setBlocks(final ArrayList<Block> pBlocks,
            final int pCandleWidth) {
        blocks = pBlocks;
        candleWidth = pCandleWidth;
        pointsChanged = true;
    }
    /**
     * Sets the time corresponding to the last pixel.
     * @param zsdt **The new final time value of the window**
     */
    public final void setTimeEnd(final ZonedStockDateTime zsdt) {
        this.endTime = zsdt;
    }
    /**
     * Sets the bottom of the y-range.
     * @param pLowY **this is the new bottom y-value**
     */
    public final void setLowY(final double pLowY) {
        this.lowY = pLowY;
    }
    /**
     * Sets the top of the y-range.
     * @param pHighY **this is the new top y-value**
     */
    public final void setHighY(final double pHighY) {
        this.highY = pHighY;
    }
    /**
     * Updates the points to be drawn.
     * @param pPoints **the array of points to be drawn**
     */
    public final void setPoints(final ArrayList<Block> pBlocks) {
        blocks = pBlocks;
    }
    public final void setTextLocation(int x, int y, boolean fromBottom,
            boolean fromLeft) {
        textX = x;
        textY = y;
    }
    /**
     * Sets the name for our display.
     * @param pName 
     */
    public final void setValueName(String pName) {
        name = pName;
    }
    /**
     * Sets the pattern for our value display.
     * @param pattern 
     */
    public final void setFormatter (String pattern) {
        formatter = new DecimalFormat(pattern);
    }
    /**
     * Checks to see if the window has changed in any way.
     * @return true if the window has changed in any way.
     */
    private boolean isChanged() {
        if (pointsChanged) {
            pointsChanged = false;
            return true;
        }
        if (blocksImage.getWidth() != getWidth()
                || blocksImage.getHeight() != getHeight()) {
            return true;
        }
        return (!imageStartTime.equals(startTime)
                || !imageEndTime.equals(endTime)
                || imageHighY != highY
                || imageLowY != lowY
                || imageTopMargin != topMargin
                || imageBottomMargin != bottomMargin
                || imageLeftMargin != leftMargin
                || imageRightMargin != rightMargin);
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
    }
    @Override
    public void componentResized(ComponentEvent ce) {
        setBounds(ce.getComponent().getBounds());
    }

    @Override
    public void componentMoved(ComponentEvent ce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void setMouseClickLocation(Point me) {
        
    }
    
}
