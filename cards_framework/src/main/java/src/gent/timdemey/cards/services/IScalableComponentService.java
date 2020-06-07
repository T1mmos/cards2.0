package gent.timdemey.cards.services;

import java.util.UUID;

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
    public void updateResources(Runnable callback);
    
    /**
     * Inserts into the managed scalable components, their required resources in the
     * current scale. 
     */
    public void updateComponents();

    /**
     * Adds a scalable resource.
     * @param scaleRes
     */
    public void addScalableResource(IScalableResource scaleRes);
    
    /**
     * Adds a scalable component.
     * @param scaleComp
     */
    public void addScalableComponent(IScalableComponent scaleComp);
    
    /**
     * Get a ScalableImage mapped to the given managed object.
     * 
     * @param card
     * @return
     */
    public IScalableComponent getScalableComponent(UUID id);
    
    /**
     * Get a ScalableResource mapped to the given id.
     * @param id
     * @return
     */
 //   public IScalableResource getScalableResource(UUID id);

    /**
     * Clears all links between ScalableImages and their managed objects. All
     * references to managed objects are cleared.
     */
    public void clearManagedObjects();
}
