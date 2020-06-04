package gent.timdemey.cards.services;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.ImageDefinition;

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
public interface IScalableComponentManager
{

    /**
     * Loads all resources in a background thread and
     * runs the given Runnable on the UI thread when all images are loaded.
     * 
     * @param paths
     * @param callback
     */
    public void loadResourcesAsync(List<ImageDefinition> imgDefs, Consumer<Boolean> onResult);

    /**
     * Sets the size of all image mapped from object of the given type the given
     * dimensions. This has no effect until calling an update() method.
     * 
     * @param clazzs
     * @param x
     * @param y
     */
    public void setSize(String group, int x, int y);

    /**
     * Rescales all images and updates the scalable components. This schedules a high
     * quality rescale of the components in background threads, and
     * waits for all rescale operations to finish before updating all
     * components at the same time on the UI thread.
     */
    public void rescaleAsync(Runnable callback);

    /**
     * Sets the image in the ScalableImage mapped from the given object to the image
     * identified by the given path. The group of the path must equal the group of
     * the current path of the object.
     * 
     * @param objects
     */
    public void setImage(UUID id, String path);

    /**
     * Get a ScalableImage mapped to the given managed object.
     * 
     * @param card
     * @return
     */
    public IScalableComponent getScalableComponent(UUID id);

    /**
     * Clears all links between ScalableImages and their managed objects. All
     * references to managed objects are cleared.
     */
    public void clearManagedObjects();

    /**
     * Get the object associated to the scalable component.
     * 
     * @param card
     * @return
     */
    public UUID getUUID(IScalableComponent scaleComp);
}
