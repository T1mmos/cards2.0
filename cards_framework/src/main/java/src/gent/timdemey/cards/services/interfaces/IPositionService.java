package gent.timdemey.cards.services.interfaces;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

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
     * Returns the bounds of a element identified by the given id.
     * @param id
     * @return
     */
    public Rectangle getBounds(UUID id);   
    
    /**
     * Returns all components of type T found at the given point in the container.
     * @param <T>
     * @param clazz
     * @param p
     * @return
     */
    public <T> List<T> getComponentsOfTypeAt(Class<T> clazz, Point p); 
    
    /**
     * Returns all components of type T found overlapping with the given rectangle in the container.
     * @param <T>
     * @param clazz
     * @param rect
     * @return
     */
    public <T> List<T> getComponentsOfTypeIn(Class<T> clazz, Rectangle rect);
}
