/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.candhcapital.Graphing;

import javax.swing.JSeparator;

/**
 *
 * @author Corporate
 */
public class MultiSeparator extends JSeparator{
    /**
     * The name of the panel above the separator.
     */
    private String topName;
    /**
     * The name of the panel below the separator.
     */
    private String bottomName;

    /**
     * Simple initializer.
     */
    public MultiSeparator() {
        super();
        topName = "";
        bottomName = "";
    }
    /**
     * Sets the name of the component.
     * @param name the name.
     */
    public final void setTopName(final String name) {
        topName = name;
    }
    /**
     * Sets the name of the bottom component.
     * @param name the name.
     */
    public final void setBottomName(final String name) {
        bottomName = name;
    }
    /**
     * Returns top name.
     * @return the name of the top component.
     */
    public final String getTopName() {
        return topName;
    }
    /**
     * Returns the bottom name.
     * @return the name of the bottom component.
     */
    public final String getBottomName() {
        return bottomName;
    }
    /**
     * Checks to see if the mouse is in the divider. Called after Press.
     * @param mY the mouse Y coordinate.
     * @return true if in.
     */
    public final boolean checkMouseY(final int mY) {
        int y = (int) this.getBounds().getY();
        return (mY >= y - getBounds().getHeight() * 2
                && mY <= y + getBounds().getHeight());
    }
}
