package gent.timdemey.cards.services.scaleman;

public abstract class ScalableResource implements IScalableResource
{
    public final String id;
    
    /**
     * If true, it indicates that the resource is not the intended resource because
     * it couldn't be loaded (e.g. because of I/O error). This resource object
     * is using a fallback resource. For example, in case of image resources,
     * a programmatically generated image could be used.
     */
    public final boolean fallback;
    
    public ScalableResource (String id, boolean fallback)
    {
        this.id = id;
        this.fallback = fallback;
    }
    
    @Override
    public String getId()
    {
        return id;
    }
}
