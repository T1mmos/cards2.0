package gent.timdemey.cards.services.interfaces;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;

/**
 * Creates and manages JScalableImages. You create them by supplying any object
 * which is then transformed into a JScalableImage by taking the appropriate
 * factory based on the object's type. From then on, the JScalableImage is
 * managed internally, and the mapping from JScalableImage to its backing object
 * and vica versa can be requested.
 * <p>
 * All managed images are updated with a high quality image upon calling
 * updateScale().
 * 
 * @author Timmos
 */
public interface IScalingService
{
    /**
     * Rescales all resources in a background thread. This schedules a high
     * quality rescale of the components in background threads, and
     * waits for all rescale operations to finish before updating all
     * components at the same time on the UI thread.
     */
    public void rescaleAsync(List<RescaleRequest> requests, Runnable callback);    

    /**
     * Get or creates once a scalable component for the given model object.
     * @param card
     * @return
     */
    public IScalableComponent getOrCreateScalableComponent(ReadOnlyCard card);
    
    /**
     * Get or creates once a scalable component for the given model object.
     * @param card
     * @return
     */
    public IScalableComponent getOrCreateScalableComponent(ReadOnlyCardStack card);
    
    /**
     * Adds a scalable resource.
     * @param scaleRes
     */
    public void addScalableResource(IScalableResource<?> scaleRes);
    
    public void addScalableComponent(IScalableComponent scaleComp);
            
    /**
     * Get a scalable component mapped to the given id.
     * 
     * @param card
     * @return
     */
    public IScalableComponent getScalableComponent(UUID compId);

    /**
     * Clears all links between ScalableImages and their managed objects. All
     * references to managed objects are cleared.
     */
    public void clearManagedObjects();    

    /**
     * Gets the component at the given position with the highest Z-order.
     * @param p
     * @return
     */
    public IScalableComponent getComponentAt(Point p);
        
    /**
     * Get all components found at the given position, disregarding their Z-order.
     * @param p
     * @return
     */
    public List<IScalableComponent> getComponentsAt(Point p);
    
    /**
     * Get all components overlapping with the given rectangle, disregarding their Z-order.
     * @param p
     * @return
     */
    public List<IScalableComponent> getComponentsIn(Rectangle rect);
    
    /**
     * Get the components found at the given position that are of the given type.
     * @param p
     * @param clazz
     * @return
     */
    public <T extends IScalableComponent> List<T> getComponentsAt(Point p, Class<T> clazz);  
    
    public List<IScalableComponent> getComponents();

    /**
     * Gets the scalable resource with the given id.
     * @param resId
     * @return
     */
    public IScalableResource<?> getScalableResource(UUID resId);
   
}
