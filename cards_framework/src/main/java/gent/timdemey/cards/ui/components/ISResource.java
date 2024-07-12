package gent.timdemey.cards.ui.components;

import java.awt.Dimension;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.services.contract.GetResourceResponse;
import gent.timdemey.cards.services.contract.res.Resource;

public interface ISResource<R>
{
    /**
     * The unique id of this resource. Most of the time, this is a file path.
     * @return
     */
    UUID getId();
    
    /**
     * Rescales the original raw resource in the requested
     * dimension. The result may be cached by the implementation's caching policy (if any) so the rescaling
     * operation may be very fast.
     * @param dim
     */
    void rescale(Dimension dim);
    
    /**
     * Get the raw resource in the given dimensions. The returned resource may
     * not be in the requested dimensions in which case it was not yet cached. (the background 
     * rescale operation has yet to finish for all resources). In this case, it is up to the component to apply a
     * rescale operation on the UI thread optimized for speed.
     * @return
     */
    GetResourceResponse<R> get(Dimension dim);
    
    /**
     * Get the actual resource in its original dimensions.
     * @return
     */
    Resource<R> getResource();
    
    /**
     * Get all available dimensions of this resource.
     * @return
     */
    List<Dimension> getDimensions();
}
