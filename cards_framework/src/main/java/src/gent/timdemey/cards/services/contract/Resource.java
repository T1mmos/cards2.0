package gent.timdemey.cards.services.contract;

public abstract class Resource
{
    /**
     * Indicates that this resource is infact a fallback because the intended
     * resource could not be loaded. For example, an image could not be loaded
     * from disk, thus a fallback image was generated programmatically.
     */
    public final boolean fallback;
    
    /**
     * The name of the file where the resource was loaded from (or at least an
     * attempt was made).
     */
    public final String filename;
    
    public Resource(String filename, boolean fallback)
    {
        this.filename = filename;
        this.fallback = fallback;
    }
}
