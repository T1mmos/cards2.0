package gent.timdemey.cards.services.scaleman;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Creates and manages JScalableImages. You create them by supplying any object which is
 * then transformed into a JScalableImage by taking the appropriate factory based on the 
 * object's type. From then on, the JScalableImage is managed internally, and the mapping
 * from JScalableImage to its backing object and vica versa can be requested.
 * <p>
 * All managed images are updated with a high quality image upon calling updateScale().
 * @author Timmos
 */
public interface IScalableImageManager {
    
    /**
     * Loads all images mapped from the given objects in a background thread and runs the given 
     * Runnable on the UI thread when all images are loaded.  
     * @param paths
     * @param callback
     */
    public void loadImages (List<ImageDefinition> imgDefs, Consumer<Boolean> onResult);
    
    /**
     * Sets the size of all image mapped from object of the given type the given dimensions.
     * This has no effect until calling an update() method. 
     * @param clazzs
     * @param x
     * @param y
     */
    public void setSize(String group, int x, int y);
    
    /**
     * Rescales all images and updates the JScalableImages. This schedules a high quality rescale of the original 
     * buffered images in background threads, and waits for all rescale operations to finish before
     * updating all JScalableImages at the same time on the UI thread.
     */
    public void apply(Runnable callback);
    
    /**
     * Sets the image in the JScalable mapped from the given object to the image identified by the given 
     * path. The group of the path must equal the group of the current path of the object.
     * @param objects
     */
    public void setImage(UUID id, String path);
        
    /**
     * Get a JScalableImage mapped to the given managed object.
     * @param card
     * @return
     */
    public JScalableImage getJScalableImage (UUID id);
    
    /**
     * Clears all links between JScalableImages and their managed objects. All
     * references to managed objects are cleared. 
     */
    public void clearManagedObjects();
    
    /**
     * Get the object associated to the JScalableImage.
     * @param card
     * @return
     */
    public UUID getUUID (JScalableImage jcard);
}
