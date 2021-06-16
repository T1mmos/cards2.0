package gent.timdemey.cards.services.interfaces;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.ui.components.ISComponent;
import gent.timdemey.cards.ui.components.ISResource;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.SImage;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.SText;
import gent.timdemey.cards.ui.panels.IPanelManager;

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
     * Creates a scalable component for the given model object.
     * @param card
     * @return
     */
    public ISComponent createSComponent(ReadOnlyCard card, IPanelManager panelManager);
    
    /**
     * Gets a scalable component for the given model object.
     * @param card
     * @return
     */
    public ISComponent getSComponent(ReadOnlyCard card);
    
    /**
     * Get or creates once a scalable component for the given model object.
     * @param card
     * @return
     */
    public ISComponent createSComponent(ReadOnlyCardStack cardStack, IPanelManager panelManager);
    
    /**
     * Gets a scalable component for the given model object.
     * @param card
     * @return
     */
    public ISComponent getSComponent(ReadOnlyCardStack cardStack);
    
    /**
     * Creates a scalable image component.
     * @param compId
     * @param compType
     * @param panelMgr
     * @param payload
     * @param imgResources
     * @return
     */
    public SImage createSImage(UUID compId, ComponentType compType, IPanelManager panelMgr, Object payload, SImageResource ... imgResources);
    
    public SText createSText(UUID compId, ComponentType compType, String text, IPanelManager panelMgr, Object payload, SFontResource textRes);
    
    /**
     * Adds a scalable resource.
     * @param scaleRes
     */
    public void addSResource(ISResource<?> scaleRes);
    
    public void addSComponent(ISComponent scaleComp);
            
    /**
     * Get a scalable component mapped to the given id.
     * 
     * @param card
     * @return
     */
    public ISComponent getSComponent(UUID compId);

    /**
     * Clears the cache containing scalable components. 
     */
    public void clearComponentCache();    
    
    /**
     * Clears the cache containing scalable resources. The component cache 
     * needs to be cleared first. 
     */
    public void clearResourceCache();    
        
    /**
     * Gets the component managed by the given panel manager 
     * at the given position with the highest Z-order.
     * @param p
     * @return
     */
    public ISComponent getComponentAt(IPanelManager panelMgr, Point p);
        
    /**
     * Get all components found at the given position, disregarding their Z-order.
     * @param p
     * @return
     */
    public List<ISComponent> getSComponentsAt(Point p);
    
    /**
     * Get all components overlapping with the given rectangle, disregarding their Z-order.
     * @param p
     * @return
     */
    public List<ISComponent> getSComponentsIn(Rectangle rect);
    
    /**
     * Get the components found at the given position that are of the given type.
     * @param p
     * @param clazz
     * @return
     */
    public <T extends ISComponent> List<T> getComponentsAt(Point p, Class<T> clazz);  
    
    public List<ISComponent> getSComponents();

    /**
     * Gets the scalable resource with the given id.
     * @param resId
     * @return
     */
    public ISResource<?> getSResource(UUID resId);

   
}
