package gent.timdemey.cards.services.interfaces;

import java.awt.Dimension;
import java.awt.Rectangle;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.GetScaleInfoRequest;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.scaling.IScalableComponent;

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
    public LayeredArea getLayeredArea(IScalableComponent scaleComp);
   
    /**
     * Uses the given relative coordinates to create a new coordinates object with the
     * matching absolute coordinates filled in.
     * @param original
     * @return
     */
    public Coords getAbsolute(Coords original);
    /**
     * Gets the dimensions given a request that contains runtime context where a resource
     * could be used. This allows to pre-scale resources without knowing the actual
     * components where they could be used in the future (those components may not even 
     * exist yet). 
     * <p>A good example is an animation shown after a card is put down onto a stack:
     * this animation is short-lived and the component holding the shown resource does not
     * need to exist at the point in time where the container is resized.
     * @param request
     * @return
     */
    public Dimension getDimension(GetScaleInfoRequest request);
    
    /**
     * Gets the drag layer, the layer in which components should be positioned when 
     * a component is manually dragged.
     * @return
     */
    public int getDragLayer();
    
    public int getAnimationLayer();
}
