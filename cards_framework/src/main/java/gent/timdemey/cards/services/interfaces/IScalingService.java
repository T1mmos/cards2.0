package gent.timdemey.cards.services.interfaces;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.ui.components.ISResource;

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
     * quality rescale of the comp2jcomp in background threads, and
     * waits for all rescale operations to finish before updating all
     * comp2jcomp at the same time on the UI thread.
     */
    public void rescaleAsync(List<RescaleRequest> requests, Runnable callback);    

    /**
     * Adds a scalable resource.
     * @param scaleRes
     */
    public void addSResource(ISResource<?> scaleRes);
                
    /**
     * Clears the cache containing scalable resources. The component cache 
     * needs to be cleared first. 
     */
    public void clearResourceCache();    
  
    /**
     * Gets the scalable resource with the given id.
     * @param resId
     * @return
     */
    public ISResource<?> getSResource(UUID resId);

   
}
