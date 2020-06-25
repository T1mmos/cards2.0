package gent.timdemey.cards.services.interfaces;

import java.awt.Rectangle;

import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.scaleman.IScalableComponent;

/**
 * Calculates the entire game panel layout given a maximum width and height.
 * @author Tim
 *
 */
public interface IPositionService
{
    /**
     * Sets the container maximum bounds and calculates the new current bounds of the
     * container and its children according to the available space. All bounds can then
     * be requested by calling the appropriate getters.
     * @param maxWidth
     * @param maxHeight
     */
    public void setMaxSize(int maxWidth, int maxHeight);

    /**
     * Returns the size of the container.
     * @return
     */
    public Rectangle getBounds();

    /**
     * Returns the bounds where an element identified by the given id should be positioned at.
     * @param id
     * @return
     */
    public LayeredArea getLayeredArea(IScalableComponent<?> scaleComp);   

    /**
     * Gets the drag layer, the layer in which components should be positioned when 
     * a component is manually dragged.
     * @return
     */
    public int getDragLayer();
    
    public int getAnimationLayer();
}
