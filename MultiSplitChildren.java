package net.candhcapital.Graphing;


import java.util.LinkedHashMap;
import javax.swing.JComponent;

/**
 *
 * @author Corporate
 */
public class MultiSplitChildren extends LinkedHashMap<String, MultiSplitChild> {

    /**
     * Empty constructor. Could probably be done without extending the
     * LinkedHashMap but this just made it so much easier.
     */
    public MultiSplitChildren() {
        super();
    }
    /**
     * Add a child. The names must be unique. THIS DOES NOT CALCULATE A NEW
     * WEIGHT SO YOU MUST MAKE SURE THE WEIGHT SUMS TO 1.
     * @param msc A MultiSplitChild.
     */
    public final void add(final MultiSplitChild msc) {
        if (!containsKey(msc.getName())) {
            put(msc.getName(), msc);
        } else {
            throw new Error("Non-unique name used to add child");
        }
    }
    /**
     * Add a child. Scales all the old percentages to make room.
     * @param name the name to reference the child. Must be unique.
     * @param component the component to display on the screen.
     * @param weight the weight from 0 to 1 of the child. 1 is 100%.
     */
    public final void add(final String name, final JComponent component,
            final double weight) {
        double newPercentage = 1 - weight;
        for (MultiSplitChild c : values()) {
            c.updateWeight(newPercentage);
        }
        put(name, new MultiSplitChild(name, component, weight));
    }
    /**
     * Removes a child, should recalculate the weights so the scales stay
     * the same.
     * @param name the name of the child to remove.
     */
    public final void removeChild(final String name) {
        double newPercentage = 1 - get(name).getWeight();
        remove(name);
        values().stream().forEach((c) -> {
            c.updateWeight(1 / newPercentage);
        });
    }
    /**
     * Returns the child weight.
     * @param name the name of the child.
     * @return the weight of the child from 0 to 1.
     */
    public final double getChildWeight(final String name) {
        return get(name).getWeight();
    }
    /**
     * Sets the child weight. Not designed to be used manually.
     * @param name the name of the child to set.
     * @param weight the weight to set.
     */
    public final void setChildWeight(final String name, final double weight) {
        get(name).setWeight(weight);
    }
    /**
     * Returns the child height.
     * @param name the name of the child.
     * @return the height of the child (in pixels)
     */
    public final double getChildHeight(final String name) {
        return get(name).getComponent().getBounds().getHeight();
    }
}
