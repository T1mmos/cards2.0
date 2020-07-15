package gent.timdemey.cards.services.interfaces;

import java.awt.Dimension;

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
     * Returns the packed bounding box of the container where all content exactly fits in without any 
     * margins. 
     * @return
     */
    public Coords.Absolute getPackedCoords();

    /**
     * Given relative coordinates, converts them into absolute coordinates for size of the current container, 
     * followed by adding offsets that keeps the packed container centered into its parent.
     * @return
     */
    public Coords.Absolute getAbsoluteCoords(Coords.Relative relcoords);
    
    /**
     * Given absolute coordinates, subtracts any offsets and converts this result into relative coordinates using 
     * the size of the current container.
     * @param scaleComp
     * @return
     */
    public Coords.Relative getRelativeCoords(Coords.Absolute abscoords);
    
    /**
     * Returns the bounds where the given component should be initially positioned at.
     * @param id
     * @return
     */
    public LayeredArea getStartLayeredArea(IScalableComponent scaleComp);
    
    /**
     * Returns the bounds where the given component should be finally positioned at.
     * All components should be supported by the implementation.
     * @param id
     * @return
     */
    public LayeredArea getEndLayeredArea(IScalableComponent scaleComp);
       
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
     * Gets the drag layer, the base / minimum layer in which components should be z-ordered when 
     * a component is manually dragged.
     * @return
     */
    public int getDragLayer();
    
    /**
     * Gets the animation layer, the base / minimum layer where all components currently being 
     * animated should be z-ordered.
     * @return
     */
    public int getAnimationLayer();
}
