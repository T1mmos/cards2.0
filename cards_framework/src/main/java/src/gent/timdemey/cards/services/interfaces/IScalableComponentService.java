package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.IScalableResource;

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
public interface IScalableComponentService
{
    /**
     * Rescales all resources in a background thread. This schedules a high
     * quality rescale of the components in background threads, and
     * waits for all rescale operations to finish before updating all
     * components at the same time on the UI thread.
     */
    public void rescaleResources(Runnable callback);
    
    /**
     * Inserts into the managed scalable components, their required resources in the
     * current scale. 
     */
    public void updateComponents();

    /**
     * Get or creates once a scalable component for the given model object.
     * @param card
     * @return
     */
    public IScalableComponent getOrCreate(ReadOnlyCard card);
    
    /**
     * Adds a scalable resource.
     * @param scaleRes
     */
    public void addScalableResource(IScalableResource scaleRes);
            
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

    public void setAllBounds();
}
