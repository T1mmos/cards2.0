package gent.timdemey.cards.services.scaleman;

public interface IScalableResource
{
    /**
     * Rescale the managed resource, keeping the new rescaled instance in a
     * inaccessible field.
     * @param width
     * @param height
     */
    void rescale(int width, int height);
    
    /**
     * After all resources have be rescaled, this method is called. The resource
     * should update its internal current reference to the most up-to-date scaled
     * resource, so scalable components can now access it.
     */
    void publish();
    
    /**
     * Get the current resource.
     * @return
     */
    Object get();
}
