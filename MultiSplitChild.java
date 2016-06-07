package net.candhcapital.Graphing;

import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JComponent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Corporate
 */
public class MultiSplitChild {
    /**
     * The component to be shown in the MultiSplitPane.
     */
    private final JComponent component;
    /**
     * The weight of the component in a 0 to 1 scale. 1 being 100%.
     */
    private double weight;
    /**
     * Need a way to reference these different panes.
     */
    private final String name;

    /**
     * Simple initializer, need to know all three of these in order to make
     * this work.
     * @param n the name of the child.
     * @param c the component.
     * @param w the weight of the child from 0 to 1, 1 being 100%.
     */
    public MultiSplitChild(final String n, final JComponent c, final double w) {
        name = n;
        component = c;
        weight = w;
    }
    /**
     * Getter for the name of the child.
     * @return the name of the child.
     */
    public final String getName() {
        return name;
    }
    /**
     * Getter for the weight of the child.
     * @return the weight of the child from 0 to 1, 1 being 100%.
     */
    public final double getWeight() {
        return weight;
    }
    /**
     * Setter for the weight. Weight needs to be between 0 and 1 inclusive.
     * @param pWeight the weight from 0 to 1.
     */
    public final void setWeight(final double pWeight) {
        if (weight >= 0 && weight <= 1) {
            weight = pWeight;
        } else {
            throw new Error("Weight must be between 0 and 1 inclusive.");
        }
    }
    /**
     * When a new component is added, all of the others must make space for it.
     * This scales the old weights so they fit in the new space allocated for
     * them.
     * @param modifier number between 0 and 1, inclusive.
     */
    public final void updateWeight(final double modifier) {
        weight *= modifier;
    }
    /**
     * Since our MultiSplitPane uses a null layout, all of the components must
     * have their bounds set manually.
     * @param rectangle the bounds.
     */
    public final void setBounds(final Rectangle rectangle) {
        component.setBounds(rectangle);
    }
    /**
     * Our MultiSplitPane needs to be able to access the components in order
     * to display them.
     * @return the component of the child.
     */
    public final JComponent getComponent() {
        return component;
    }
}
